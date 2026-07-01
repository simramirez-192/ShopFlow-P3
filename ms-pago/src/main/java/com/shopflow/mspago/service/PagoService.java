package com.shopflow.mspago.service;

import com.shopflow.mspago.client.NotificacionClient;
import com.shopflow.mspago.client.OrdenClient;
import com.shopflow.mspago.dto.PagoRequestDTO;
import com.shopflow.mspago.dto.PagoResponseDTO;
import com.shopflow.mspago.model.Pago;
import com.shopflow.mspago.repository.PagoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    // FeignClients inyectados igual que @Repository
    private final OrdenClient ordenClient;
    private final NotificacionClient notificacionClient;

    // ── Valida y obtiene datos de la orden en ms-orden ────────
    private Map<String, Object> obtenerOrden(Long ordenId) {
        try {
            Map<String, Object> orden = ordenClient.obtenerPorId(ordenId);
            log.info(">>> Orden {} validada en ms-orden", ordenId);
            return orden;
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("La orden con id=" + ordenId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con ms-orden: " + e.getMessage());
        }
    }

    // ── Notifica al usuario que su pago fue aprobado ──────────
    private void notificarPagoAprobado(Long usuarioId, Long ordenId, String referencia) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("usuarioId", usuarioId);
            body.put("tipo", "PAGO_CONFIRMADO");
            body.put("titulo", "Pago confirmado");
            body.put("mensaje", "Tu pago para la orden #" + ordenId + " fue aprobado. Referencia: " + referencia);
            notificacionClient.crear(body);
            log.info(">>> Notificacion PAGO_CONFIRMADO enviada para usuarioId={}", usuarioId);
        } catch (FeignException e) {
            log.warn(">>> No se pudo enviar notificacion de pago: {}", e.getMessage());
        }
    }

    private PagoResponseDTO mapToDTO(Pago p) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(p.getId());
        dto.setOrdenId(p.getOrdenId());
        dto.setMonto(p.getMonto());
        dto.setMetodoPago(p.getMetodoPago());
        dto.setEstado(p.getEstado());
        dto.setReferencia(p.getReferencia());
        dto.setFechaPago(p.getFechaPago());
        dto.setCreadoEn(p.getCreadoEn());
        return dto;
    }

    public List<PagoResponseDTO> obtenerTodos() {
        return pagoRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<PagoResponseDTO> obtenerPorOrden(Long ordenId) {
        return pagoRepository.findByOrdenId(ordenId).map(this::mapToDTO);
    }

    public PagoResponseDTO procesar(PagoRequestDTO dto) {
        // Obtiene los datos de la orden via FeignClient (valida que existe y trae usuarioId)
        Map<String, Object> orden = obtenerOrden(dto.getOrdenId());
        Long usuarioId = Long.valueOf(orden.get("usuarioId").toString());

        if (pagoRepository.findByOrdenId(dto.getOrdenId()).isPresent()) {
            throw new RuntimeException("Ya existe un pago para la orden " + dto.getOrdenId());
        }

        Pago pago = new Pago();
        pago.setOrdenId(dto.getOrdenId());
        pago.setMonto(dto.getMonto());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setEstado("APROBADO");
        pago.setReferencia("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        pago.setFechaPago(LocalDateTime.now());

        Pago guardado = pagoRepository.save(pago);
        log.info(">>> Pago procesado id={} referencia={}", guardado.getId(), guardado.getReferencia());

        // Notifica a ms-notificacion via FeignClient que el pago fue aprobado
        notificarPagoAprobado(usuarioId, dto.getOrdenId(), guardado.getReferencia());

        return mapToDTO(guardado);
    }
}
