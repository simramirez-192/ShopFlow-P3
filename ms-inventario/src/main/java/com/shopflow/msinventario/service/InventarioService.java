package com.shopflow.msinventario.service;

import com.shopflow.msinventario.client.ProductoClient;
import com.shopflow.msinventario.dto.InventarioRequestDTO;
import com.shopflow.msinventario.dto.InventarioResponseDTO;
import com.shopflow.msinventario.model.Inventario;
import com.shopflow.msinventario.model.MovimientoStock;
import com.shopflow.msinventario.repository.InventarioRepository;
import com.shopflow.msinventario.repository.MovimientoStockRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class InventarioService {

    private final InventarioRepository inventarioRepository;
    private final MovimientoStockRepository movimientoRepository;
    // FeignClient inyectado igual que un @Repository
    private final ProductoClient productoClient;

    // ── Valida que el producto exista en ms-producto ──
    private void validarProducto(Long productoId) {
        if (productoId == null) return;
        try {
            productoClient.obtenerPorId(productoId);
            log.info(">>> Producto {} validado en ms-producto", productoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El producto con id=" + productoId + " no existe en ms-producto.");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con ms-producto: " + e.getMessage());
        }
    }

    private InventarioResponseDTO mapToDTO(Inventario inv) {
        InventarioResponseDTO dto = new InventarioResponseDTO();
        dto.setId(inv.getId()); dto.setProductoId(inv.getProductoId());
        dto.setStock(inv.getStock()); dto.setStockMinimo(inv.getStockMinimo());
        dto.setUbicacion(inv.getUbicacion());
        dto.setStockBajo(inv.getStock() <= inv.getStockMinimo());
        return dto;
    }

    public List<InventarioResponseDTO> obtenerTodos() {
        return inventarioRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<InventarioResponseDTO> obtenerPorProducto(Long productoId) {
        return inventarioRepository.findByProductoId(productoId).map(this::mapToDTO);
    }

    public InventarioResponseDTO crear(InventarioRequestDTO dto) {
        validarProducto(dto.getProductoId());
        if (inventarioRepository.findByProductoId(dto.getProductoId()).isPresent()) {
            throw new RuntimeException("Ya existe inventario para el producto " + dto.getProductoId());
        }
        Inventario inv = new Inventario();
        inv.setProductoId(dto.getProductoId()); inv.setStock(dto.getStock());
        inv.setStockMinimo(dto.getStockMinimo() != null ? dto.getStockMinimo() : 5);
        inv.setUbicacion(dto.getUbicacion());
        return mapToDTO(inventarioRepository.save(inv));
    }

    public InventarioResponseDTO ajustarStock(Long productoId, InventarioRequestDTO dto) {
        Inventario inv = inventarioRepository.findByProductoId(productoId)
                .orElseThrow(() -> new RuntimeException("No hay registro de inventario para producto=" + productoId));
        int nuevoStock;
        if ("ENTRADA".equalsIgnoreCase(dto.getTipo())) {
            nuevoStock = inv.getStock() + dto.getCantidad();
        } else if ("SALIDA".equalsIgnoreCase(dto.getTipo())) {
            if (inv.getStock() < dto.getCantidad())
                throw new RuntimeException("Stock insuficiente. Disponible: " + inv.getStock());
            nuevoStock = inv.getStock() - dto.getCantidad();
        } else {
            throw new RuntimeException("Tipo debe ser ENTRADA o SALIDA");
        }
        inv.setStock(nuevoStock);
        inventarioRepository.save(inv);
        movimientoRepository.save(new MovimientoStock(null, productoId, dto.getTipo().toUpperCase(), dto.getCantidad(), dto.getMotivo(), null));
        log.info(">>> Stock ajustado: producto={} tipo={} cantidad={} nuevo_stock={}", productoId, dto.getTipo(), dto.getCantidad(), nuevoStock);
        return mapToDTO(inv);
    }
}
