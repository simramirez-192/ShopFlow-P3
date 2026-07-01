package com.shopflow.msautenticacion.service;

import com.shopflow.msautenticacion.client.UsuarioClient;
import com.shopflow.msautenticacion.dto.CredencialRequestDTO;
import com.shopflow.msautenticacion.dto.CredencialResponseDTO;
import com.shopflow.msautenticacion.model.Credencial;
import com.shopflow.msautenticacion.repository.CredencialRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class CredencialService {

    private final CredencialRepository credencialRepository;
    // FeignClient inyectado igual que un @Repository
    private final UsuarioClient usuarioClient;

    // ── Valida que el usuario exista en ms-usuario ────────────
    private void validarUsuario(Long usuarioId) {
        if (usuarioId == null) return;
        try {
            usuarioClient.obtenerPorId(usuarioId);
            log.info(">>> Usuario {} validado en ms-usuario", usuarioId);
        } catch (FeignException.NotFound e) {
            throw new RuntimeException("El usuario con id=" + usuarioId + " no existe en ms-usuario.");
        } catch (FeignException e) {
            throw new RuntimeException("No se puede conectar con ms-usuario: " + e.getMessage());
        }
    }

    // ── Mapeo Entidad → ResponseDTO ───────────────────────────
    private CredencialResponseDTO mapToDTO(Credencial c) {
        CredencialResponseDTO dto = new CredencialResponseDTO();
        dto.setId(c.getId());
        dto.setUsuarioId(c.getUsuarioId());
        dto.setUsername(c.getUsername());
        dto.setRol(c.getRol());
        dto.setActivo(c.getActivo());
        return dto;
    }

    public List<CredencialResponseDTO> obtenerTodas() {
        log.info(">>> Obteniendo todas las credenciales");
        return credencialRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public Optional<CredencialResponseDTO> obtenerPorId(Long id) {
        log.info(">>> Buscando credencial con id={}", id);
        return credencialRepository.findById(id).map(this::mapToDTO);
    }

    public Optional<CredencialResponseDTO> obtenerPorUsuarioId(Long usuarioId) {
        log.info(">>> Buscando credencial para usuarioId={}", usuarioId);
        return credencialRepository.findByUsuarioId(usuarioId).map(this::mapToDTO);
    }

    public CredencialResponseDTO crear(CredencialRequestDTO dto) {
        log.info(">>> Creando credencial para usuarioId={}", dto.getUsuarioId());

        // Llama a ms-usuario via FeignClient para verificar que el usuario existe
        validarUsuario(dto.getUsuarioId());

        if (credencialRepository.existsByUsername(dto.getUsername())) {
            log.warn(">>> Username '{}' ya esta en uso", dto.getUsername());
            throw new RuntimeException("El username '" + dto.getUsername() + "' ya esta en uso.");
        }

        Credencial credencial = new Credencial();
        credencial.setUsuarioId(dto.getUsuarioId());
        credencial.setUsername(dto.getUsername());
        credencial.setPassword(dto.getPassword());
        credencial.setRol(dto.getRol() != null ? dto.getRol() : "CLIENTE");

        Credencial guardada = credencialRepository.save(credencial);
        log.info(">>> Credencial creada con id={} para usuarioId={}", guardada.getId(), dto.getUsuarioId());
        return mapToDTO(guardada);
    }

    public Optional<CredencialResponseDTO> login(CredencialRequestDTO dto) {
        log.info(">>> Intento de login para username='{}'", dto.getUsername());
        return credencialRepository.findByUsername(dto.getUsername())
                .filter(c -> c.getPassword().equals(dto.getPassword()) && c.getActivo())
                .map(c -> {
                    log.info(">>> Login exitoso para username='{}'", dto.getUsername());
                    return mapToDTO(c);
                });
    }

    public Optional<CredencialResponseDTO> actualizar(Long id, CredencialRequestDTO dto) {
        return credencialRepository.findById(id).map(existente -> {
            log.info(">>> Actualizando credencial id={}", id);
            existente.setPassword(dto.getPassword());
            existente.setRol(dto.getRol() != null ? dto.getRol() : existente.getRol());
            return mapToDTO(credencialRepository.save(existente));
        });
    }

    public void eliminar(Long id) {
        log.info(">>> Eliminando credencial id={}", id);
        credencialRepository.deleteById(id);
    }
}
