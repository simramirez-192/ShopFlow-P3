package com.shopflow.msorden.service;

import com.shopflow.msorden.client.InventarioClient;
import com.shopflow.msorden.client.NotificacionClient;
import com.shopflow.msorden.client.ProductoClient;
import com.shopflow.msorden.client.UsuarioClient;
import com.shopflow.msorden.dto.OrdenRequestDTO;
import com.shopflow.msorden.dto.OrdenResponseDTO;
import com.shopflow.msorden.model.ItemOrden;
import com.shopflow.msorden.model.Orden;
import com.shopflow.msorden.repository.OrdenRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class OrdenService {

    private final OrdenRepository ordenRepository;
    // FeignClients inyectados igual que @Repository
    private final UsuarioClient usuarioClient;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;
    private final NotificacionClient notificacionClient;

    // ── Valida que el usuario existe en ms-usuario ────────────
    private void validarUsuario(Long usuarioId) {
        try {
            usuarioClient.obtenerPorId(usuarioId);
            log.info(">>> Usuario {} validado en ms-usuario", usuarioId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El usuario con id=" + usuarioId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con ms-usuario: " + e.getMessage());
        }
    }

    // ── Consulta datos del producto en ms-producto ────────────
    private Map<String, Object> obtenerProducto(Long productoId) {
        try {
            return productoClient.obtenerPorId(productoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Producto con id=" + productoId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("Error al consultar ms-producto: " + e.getMessage());
        }
    }

    // ── Descuenta stock en ms-inventario via FeignClient ─────
    private void descontarStock(Long productoId, Integer cantidad) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("tipo", "SALIDA");
            body.put("cantidad", cantidad);
            body.put("motivo", "Orden creada");
            inventarioClient.ajustarStock(productoId, body);
        } catch (FeignException e) {
            log.warn(">>> No se pudo descontar stock para productoId={}: {}", productoId, e.getMessage());
        }
    }

    // ── Notifica a ms-notificacion cuando se crea una orden ───
    private void notificarOrdenCreada(Long usuarioId, Long ordenId, BigDecimal total) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("tipo", "ORDEN_CREADA");
            body.put("titulo", "Tu orden fue recibida");
            body.put("mensaje", "Tu orden #" + ordenId + " fue recibida. Total: $" + total);
            notificacionClient.crear(body);
            log.info(">>> Notificacion ORDEN_CREADA enviada para usuarioId={}", usuarioId);
        } catch (FeignException e) {
            log.warn(">>> No se pudo enviar notificacion: {}", e.getMessage());
        }
    }

    private OrdenResponseDTO mapToDTO(Orden o) {
        List<OrdenResponseDTO.Item> items = o.getItems().stream().map(i -> {
            OrdenResponseDTO.Item ir = new OrdenResponseDTO.Item();
            ir.setId(i.getId());
            ir.setProductoId(i.getProductoId());
            ir.setNombreProducto(i.getNombreProducto());
            ir.setCantidad(i.getCantidad());
            ir.setPrecioUnit(i.getPrecioUnit());
            ir.setSubtotal(i.getSubtotal());
            return ir;
        }).collect(Collectors.toList());

        OrdenResponseDTO dto = new OrdenResponseDTO();
        dto.setId(o.getId());
        dto.setUsuarioId(o.getUsuarioId());
        dto.setEstado(o.getEstado());
        dto.setTotal(o.getTotal());
        dto.setDireccionEnvio(o.getDireccionEnvio());
        dto.setNotas(o.getNotas());
        dto.setCreadoEn(o.getCreadoEn());
        dto.setItems(items);
        return dto;
    }

    public List<OrdenResponseDTO> obtenerTodas() {
        return ordenRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<OrdenResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return ordenRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<OrdenResponseDTO> obtenerPorId(Long id) {
        return ordenRepository.findById(id).map(this::mapToDTO);
    }

    public OrdenResponseDTO crear(OrdenRequestDTO dto) {
        log.info(">>> Creando orden para usuario={}", dto.getUsuarioId());

        // Valida que el usuario existe en ms-usuario via FeignClient
        validarUsuario(dto.getUsuarioId());

        Orden orden = new Orden();
        orden.setUsuarioId(dto.getUsuarioId());
        orden.setDireccionEnvio(dto.getDireccionEnvio());
        orden.setNotas(dto.getNotas());

        BigDecimal total = BigDecimal.ZERO;
        for (OrdenRequestDTO.Item itemReq : dto.getItems()) {
            Map<String, Object> producto = obtenerProducto(itemReq.getProductoId());
            BigDecimal precio = new BigDecimal(producto.get("precio").toString());
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(itemReq.getCantidad()));
            total = total.add(subtotal);

            ItemOrden item = new ItemOrden();
            item.setOrden(orden);
            item.setProductoId(itemReq.getProductoId());
            item.setNombreProducto((String) producto.get("nombre"));
            item.setCantidad(itemReq.getCantidad());
            item.setPrecioUnit(precio);
            item.setSubtotal(subtotal);
            orden.getItems().add(item);
        }
        orden.setTotal(total);
        Orden guardada = ordenRepository.save(orden);
        log.info(">>> Orden creada id={} total={}", guardada.getId(), total);

        // Descuenta stock en ms-inventario via FeignClient
        dto.getItems().forEach(i -> descontarStock(i.getProductoId(), i.getCantidad()));

        // Notifica a ms-notificacion via FeignClient que la orden fue creada
        notificarOrdenCreada(dto.getUsuarioId(), guardada.getId(), total);

        return mapToDTO(guardada);
    }

    public Optional<OrdenResponseDTO> actualizarEstado(Long id, OrdenRequestDTO dto) {
        return ordenRepository.findById(id).map(orden -> {
            log.info(">>> Orden id={} estado cambiado a '{}'", id, dto.getEstado());
            orden.setEstado(dto.getEstado().toUpperCase());
            return mapToDTO(ordenRepository.save(orden));
        });
    }
}
