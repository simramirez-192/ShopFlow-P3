package com.shopflow.mscarrito.service;

import com.shopflow.mscarrito.client.ProductoClient;
import com.shopflow.mscarrito.dto.CarritoRequestDTO;
import com.shopflow.mscarrito.dto.CarritoResponseDTO;
import com.shopflow.mscarrito.model.Carrito;
import com.shopflow.mscarrito.model.ItemCarrito;
import com.shopflow.mscarrito.repository.CarritoRepository;
import com.shopflow.mscarrito.repository.ItemCarritoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class CarritoService {

    private final CarritoRepository carritoRepository;
    private final ItemCarritoRepository itemCarritoRepository;
    // FeignClient inyectado igual que un @Repository
    private final ProductoClient productoClient;

    // ── Obtiene precio del producto desde ms-producto ──
    private BigDecimal obtenerPrecioProducto(Long productoId) {
        try {
            Map<String, Object> prod = productoClient.obtenerPorId(productoId);
            log.info(">>> Precio obtenido para productoId={}", productoId);
            return new BigDecimal(prod.get("precio").toString());
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Producto con id=" + productoId + " no encontrado en ms-producto.");
        } catch (FeignException e) {
            throw new RuntimeException("Error al consultar ms-producto: " + e.getMessage());
        }
    }

    private CarritoResponseDTO mapToDTO(Carrito c) {
        List<CarritoResponseDTO.Item> items = c.getItems().stream().map(item -> {
            CarritoResponseDTO.Item ir = new CarritoResponseDTO.Item();
            ir.setId(item.getId()); ir.setProductoId(item.getProductoId());
            ir.setCantidad(item.getCantidad()); ir.setPrecioUnit(item.getPrecioUnit());
            ir.setSubtotal(item.getPrecioUnit().multiply(BigDecimal.valueOf(item.getCantidad())));
            return ir;
        }).collect(Collectors.toList());

        BigDecimal total = items.stream().map(CarritoResponseDTO.Item::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setId(c.getId()); dto.setUsuarioId(c.getUsuarioId());
        dto.setEstado(c.getEstado()); dto.setItems(items); dto.setTotal(total);
        return dto;
    }

    public CarritoResponseDTO obtenerOCrearCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuarioId(usuarioId);
                    log.info(">>> Creando nuevo carrito para usuario={}", usuarioId);
                    return carritoRepository.save(nuevo);
                });
        return mapToDTO(carrito);
    }

    public CarritoResponseDTO agregarItem(Long usuarioId, CarritoRequestDTO dto) {
        BigDecimal precio = obtenerPrecioProducto(dto.getProductoId());
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseGet(() -> { Carrito c = new Carrito(); c.setUsuarioId(usuarioId); return carritoRepository.save(c); });

        Optional<ItemCarrito> itemExistente = itemCarritoRepository
                .findByCarritoIdAndProductoId(carrito.getId(), dto.getProductoId());

        if (itemExistente.isPresent()) {
            ItemCarrito item = itemExistente.get();
            item.setCantidad(item.getCantidad() + dto.getCantidad());
            itemCarritoRepository.save(item);
        } else {
            ItemCarrito nuevoItem = new ItemCarrito(null, carrito, dto.getProductoId(), dto.getCantidad(), precio);
            carrito.getItems().add(nuevoItem);
        }
        log.info(">>> Item productoId={} agregado al carrito de usuario={}", dto.getProductoId(), usuarioId);
        return mapToDTO(carritoRepository.save(carrito));
    }

    public CarritoResponseDTO eliminarItem(Long usuarioId, Long itemId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para usuario=" + usuarioId));
        carrito.getItems().removeIf(i -> i.getId().equals(itemId));
        return mapToDTO(carritoRepository.save(carrito));
    }

    public void vaciarCarrito(Long usuarioId) {
        Carrito carrito = carritoRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Carrito no encontrado para usuario=" + usuarioId));
        carrito.getItems().clear();
        carritoRepository.save(carrito);
        log.info(">>> Carrito vaciado para usuario={}", usuarioId);
    }
}
