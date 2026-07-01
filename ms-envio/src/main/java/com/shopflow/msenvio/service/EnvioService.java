package com.shopflow.msenvio.service;

import com.shopflow.msenvio.client.NotificacionClient;
import com.shopflow.msenvio.client.OrdenClient;
import com.shopflow.msenvio.dto.EnvioRequestDTO;
import com.shopflow.msenvio.dto.EnvioResponseDTO;
import com.shopflow.msenvio.model.Envio;
import com.shopflow.msenvio.repository.EnvioRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class EnvioService {

    private final EnvioRepository envioRepository;
    // FeignClients inyectados igual que @Repository
    private final OrdenClient ordenClient;
    private final NotificacionClient notificacionClient;

    // ── Obtiene datos de la orden (incluye usuarioId) ─────────
    private Map<String, Object> obtenerOrden(Long ordenId) {
        try {
            return ordenClient.obtenerPorId(ordenId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Orden con id=" + ordenId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("Error al conectar con ms-orden: " + e.getMessage());
        }
    }

    // ── Notifica al usuario sobre el estado del envio ─────────
    private void notificarEstadoEnvio(Long usuarioId, Long ordenId, String estado, String seguimiento) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("tipo", "ENVIO_ACTUALIZADO");
            body.put("titulo", "Estado de tu envio actualizado");
            body.put("mensaje", "Tu envio de la orden #" + ordenId + " esta ahora en estado: " + estado + ". Seguimiento: " + seguimiento);
            notificacionClient.crear(body);
            log.info(">>> Notificacion ENVIO_ACTUALIZADO enviada para usuarioId={} estado={}", usuarioId, estado);
        } catch (FeignException e) {
            log.warn(">>> No se pudo enviar notificacion de envio: {}", e.getMessage());
        }
    }

    private EnvioResponseDTO mapToDTO(Envio e) {
        EnvioResponseDTO dto = new EnvioResponseDTO();
        dto.setId(e.getId());
        dto.setOrdenId(e.getOrdenId());
        dto.setDireccionDestino(e.getDireccionDestino());
        dto.setEstado(e.getEstado());
        dto.setNumeroSeguimiento(e.getNumeroSeguimiento());
        dto.setTransportista(e.getTransportista());
        dto.setFechaEstimada(e.getFechaEstimada());
        dto.setFechaEntrega(e.getFechaEntrega());
        dto.setCreadoEn(e.getCreadoEn());
        return dto;
    }

    public List<EnvioResponseDTO> obtenerTodos() {
        return envioRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<EnvioResponseDTO> obtenerPorOrden(Long ordenId) {
        return envioRepository.findByOrdenId(ordenId).map(this::mapToDTO);
    }

    public EnvioResponseDTO crear(EnvioRequestDTO dto) {
        // Obtiene la orden via FeignClient (valida que existe y trae usuarioId)
        Map<String, Object> orden = obtenerOrden(dto.getOrdenId());
        Long usuarioId = Long.valueOf(orden.get("usuarioId").toString());

        if (envioRepository.findByOrdenId(dto.getOrdenId()).isPresent()) {
            throw new RuntimeException("Ya existe envio para la orden " + dto.getOrdenId());
        }

        Envio envio = new Envio();
        envio.setOrdenId(dto.getOrdenId());
        envio.setDireccionDestino(dto.getDireccionDestino());
        envio.setTransportista(dto.getTransportista() != null ? dto.getTransportista() : "Starken");
        envio.setNumeroSeguimiento("SF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        envio.setFechaEstimada(LocalDate.now().plusDays(3));

        Envio guardado = envioRepository.save(envio);
        log.info(">>> Envio creado id={} seguimiento={}", guardado.getId(), guardado.getNumeroSeguimiento());

        // Notifica a ms-notificacion via FeignClient que el envio fue creado
        notificarEstadoEnvio(usuarioId, dto.getOrdenId(), "PREPARANDO", guardado.getNumeroSeguimiento());

        return mapToDTO(guardado);
    }

    public Optional<EnvioResponseDTO> actualizarEstado(Long id, EnvioRequestDTO dto) {
        return envioRepository.findById(id).map(envio -> {
            envio.setEstado(dto.getEstado().toUpperCase());
            if ("ENTREGADO".equalsIgnoreCase(dto.getEstado())) {
                envio.setFechaEntrega(LocalDateTime.now());
            }
            log.info(">>> Envio id={} estado actualizado a '{}'", id, dto.getEstado());
            Envio guardado = envioRepository.save(envio);

            // Notifica a ms-notificacion el nuevo estado via FeignClient
            try {
                Map<String, Object> orden = obtenerOrden(envio.getOrdenId());
                Long usuarioId = Long.valueOf(orden.get("usuarioId").toString());
                notificarEstadoEnvio(usuarioId, envio.getOrdenId(), dto.getEstado().toUpperCase(), envio.getNumeroSeguimiento());
            } catch (Exception e) {
                log.warn(">>> No se pudo notificar cambio de estado del envio: {}", e.getMessage());
            }

            return mapToDTO(guardado);
        });
    }
}
