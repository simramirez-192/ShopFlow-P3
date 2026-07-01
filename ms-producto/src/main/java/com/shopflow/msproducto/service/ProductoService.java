package com.shopflow.msproducto.service;

import com.shopflow.msproducto.client.InventarioClient;
import com.shopflow.msproducto.dto.ProductoRequestDTO;
import com.shopflow.msproducto.dto.ProductoResponseDTO;
import com.shopflow.msproducto.model.Categoria;
import com.shopflow.msproducto.model.Producto;
import com.shopflow.msproducto.repository.CategoriaRepository;
import com.shopflow.msproducto.repository.ProductoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    // FeignClient inyectado igual que un @Repository
    private final InventarioClient inventarioClient;

    // ── Registra stock inicial en ms-inventario al crear producto ──
    private void registrarInventarioInicial(Long productoId) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("productoId", productoId);
            body.put("stock", 0);
            body.put("stockMinimo", 5);
            body.put("ubicacion", "Sin asignar");
            inventarioClient.crear(body);
            log.info(">>> Inventario inicial creado en ms-inventario para productoId={}", productoId);
        } catch (FeignException e) {
            log.warn(">>> No se pudo crear inventario inicial: {}. El producto fue creado de todas formas.", e.getMessage());
        }
    }

    private ProductoResponseDTO mapToDTO(Producto p) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(p.getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setPrecio(p.getPrecio());
        dto.setImagenUrl(p.getImagenUrl());
        dto.setActivo(p.getActivo());
        dto.setCategoriaId(p.getCategoria().getId());
        dto.setCategoriaNombre(p.getCategoria().getNombre());
        dto.setCreadoEn(p.getCreadoEn());
        return dto;
    }

    public List<ProductoResponseDTO> obtenerTodos() {
        log.info(">>> Obteniendo todos los productos activos");
        return productoRepository.findByActivoTrue().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<ProductoResponseDTO> obtenerPorId(Long id) {
        log.info(">>> Buscando producto id={}", id);
        return productoRepository.findById(id).map(this::mapToDTO);
    }

    public List<ProductoResponseDTO> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ProductoResponseDTO> obtenerPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaId(categoriaId).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        log.info(">>> Creando producto '{}'", dto.getNombre());
        Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new RuntimeException("Categoria con id=" + dto.getCategoriaId() + " no existe."));
        Producto p = new Producto();
        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setPrecio(dto.getPrecio());
        p.setImagenUrl(dto.getImagenUrl());
        p.setCategoria(cat);
        Producto guardado = productoRepository.save(p);
        log.info(">>> Producto creado id={}", guardado.getId());

        // Notifica a ms-inventario via FeignClient para crear registro de stock inicial
        registrarInventarioInicial(guardado.getId());

        return mapToDTO(guardado);
    }

    public Optional<ProductoResponseDTO> actualizar(Long id, ProductoRequestDTO dto) {
        return productoRepository.findById(id).map(existente -> {
            Categoria cat = categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new RuntimeException("Categoria no encontrada."));
            existente.setNombre(dto.getNombre());
            existente.setDescripcion(dto.getDescripcion());
            existente.setPrecio(dto.getPrecio());
            existente.setImagenUrl(dto.getImagenUrl());
            existente.setCategoria(cat);
            log.info(">>> Producto id={} actualizado", id);
            return mapToDTO(productoRepository.save(existente));
        });
    }

    public void eliminar(Long id) {
        log.info(">>> Eliminando (logico) producto id={}", id);
        productoRepository.findById(id).ifPresent(p -> {
            p.setActivo(false);
            productoRepository.save(p);
        });
    }
}
