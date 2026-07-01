package com.shopflow.msnotificacion.service;

import com.shopflow.msnotificacion.client.UsuarioClient;
import com.shopflow.msnotificacion.dto.NotificacionRequestDTO;
import com.shopflow.msnotificacion.dto.NotificacionResponseDTO;
import com.shopflow.msnotificacion.model.Notificacion;
import com.shopflow.msnotificacion.repository.NotificacionRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;
    // FeignClient inyectado igual que un @Repository
    private final UsuarioClient usuarioClient;

    // ── Valida que el usuario exista en ms-usuario ────
    private void validarUsuario(Long usuarioId) {
        try {
            usuarioClient.obtenerPorId(usuarioId);
            log.info(">>> Usuario {} validado en ms-usuario", usuarioId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("Usuario con id=" + usuarioId + " no existe.");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con ms-usuario: " + e.getMessage());
        }
    }

    private NotificacionResponseDTO mapToDTO(Notificacion n) {
        NotificacionResponseDTO dto = new NotificacionResponseDTO();
        dto.setId(n.getId()); dto.setUsuarioId(n.getUsuarioId()); dto.setTipo(n.getTipo());
        dto.setTitulo(n.getTitulo()); dto.setMensaje(n.getMensaje());
        dto.setLeida(n.getLeida()); dto.setEnviada(n.getEnviada()); dto.setCreadoEn(n.getCreadoEn());
        return dto;
    }

    public List<NotificacionResponseDTO> obtenerPorUsuario(Long usuarioId) {
        log.info(">>> Obteniendo notificaciones para usuario={}", usuarioId);
        return notificacionRepository.findByUsuarioIdOrderByCreadoEnDesc(usuarioId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<NotificacionResponseDTO> obtenerNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeida(usuarioId, false)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public NotificacionResponseDTO crear(NotificacionRequestDTO dto) {
        validarUsuario(dto.getUsuarioId());
        Notificacion n = new Notificacion();
        n.setUsuarioId(dto.getUsuarioId()); n.setTipo(dto.getTipo());
        n.setTitulo(dto.getTitulo()); n.setMensaje(dto.getMensaje());
        Notificacion guardada = notificacionRepository.save(n);
        log.info(">>> Notificacion creada id={} tipo={} para usuario={}", guardada.getId(), dto.getTipo(), dto.getUsuarioId());
        return mapToDTO(guardada);
    }

    public Optional<NotificacionResponseDTO> marcarLeida(Long id) {
        return notificacionRepository.findById(id).map(n -> {
            n.setLeida(true);
            log.info(">>> Notificacion id={} marcada como leida", id);
            return mapToDTO(notificacionRepository.save(n));
        });
    }

    public void marcarTodasLeidas(Long usuarioId) {
        List<Notificacion> noLeidas = notificacionRepository.findByUsuarioIdAndLeida(usuarioId, false);
        noLeidas.forEach(n -> n.setLeida(true));
        notificacionRepository.saveAll(noLeidas);
        log.info(">>> {} notificaciones marcadas como leidas para usuario={}", noLeidas.size(), usuarioId);
    }
}
