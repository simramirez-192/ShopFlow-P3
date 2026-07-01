package com.shopflow.msresena.service;

import com.shopflow.msresena.client.OrdenClient;
import com.shopflow.msresena.client.ProductoClient;
import com.shopflow.msresena.dto.ResenaRequestDTO;
import com.shopflow.msresena.dto.ResenaResponseDTO;
import com.shopflow.msresena.model.Resena;
import com.shopflow.msresena.repository.ResenaRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    // FeignClients inyectados igual que @Repository
    private final ProductoClient productoClient;
    private final OrdenClient ordenClient;

    private void validarProducto(Long productoId) {
        try {
            productoClient.obtenerPorId(productoId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Producto con id=" + productoId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("Error al conectar con ms-producto: " + e.getMessage());
        }
    }

    private void validarOrden(Long ordenId) {
        try {
            ordenClient.obtenerPorId(ordenId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Orden con id=" + ordenId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("Error al conectar con ms-orden: " + e.getMessage());
        }
    }

    private ResenaResponseDTO mapToDTO(Resena r) {
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setId(r.getId()); dto.setProductoId(r.getProductoId()); dto.setUsuarioId(r.getUsuarioId());
        dto.setOrdenId(r.getOrdenId()); dto.setCalificacion(r.getCalificacion());
        dto.setComentario(r.getComentario()); dto.setActiva(r.getActiva()); dto.setCreadoEn(r.getCreadoEn());
        return dto;
    }

    public List<ResenaResponseDTO> obtenerPorProducto(Long productoId) {
        log.info(">>> Obteniendo resenas para productoId={}", productoId);
        return resenaRepository.findByProductoIdAndActivaTrue(productoId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<ResenaResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public ResenaResponseDTO obtenerPromedio(Long productoId) {
        Double promedio = resenaRepository.promedioCalificacion(productoId);
        Long total = resenaRepository.totalResenas(productoId);
        ResenaResponseDTO dto = new ResenaResponseDTO();
        dto.setProductoId(productoId);
        dto.setPromedioCalificacion(promedio != null ? promedio : 0.0);
        dto.setTotalResenas(total != null ? total : 0L);
        return dto;
    }

    public ResenaResponseDTO crear(ResenaRequestDTO dto) {
        validarProducto(dto.getProductoId());
        validarOrden(dto.getOrdenId());
        if (resenaRepository.existsByProductoIdAndUsuarioIdAndOrdenId(
                dto.getProductoId(), dto.getUsuarioId(), dto.getOrdenId())) {
            throw new RuntimeException("Ya existe una resena para este producto en esta orden.");
        }
        Resena resena = new Resena();
        resena.setProductoId(dto.getProductoId()); resena.setUsuarioId(dto.getUsuarioId());
        resena.setOrdenId(dto.getOrdenId()); resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        Resena guardada = resenaRepository.save(resena);
        log.info(">>> Resena creada id={} producto={} calificacion={}", guardada.getId(), dto.getProductoId(), dto.getCalificacion());
        return mapToDTO(guardada);
    }

    public void eliminar(Long id) {
        resenaRepository.findById(id).ifPresent(r -> { r.setActiva(false); resenaRepository.save(r); });
        log.info(">>> Resena id={} desactivada", id);
    }
}
