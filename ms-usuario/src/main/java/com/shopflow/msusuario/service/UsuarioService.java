package com.shopflow.msusuario.service;

import com.shopflow.msusuario.client.AutenticacionClient;
import com.shopflow.msusuario.dto.UsuarioRequestDTO;
import com.shopflow.msusuario.dto.UsuarioResponseDTO;
import com.shopflow.msusuario.model.Usuario;
import com.shopflow.msusuario.repository.UsuarioRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    // FeignClient inyectado igual que un @Repository
    private final AutenticacionClient autenticacionClient;

    // ── Consulta si el usuario tiene credenciales en ms-autenticacion ──
    private String obtenerRolUsuario(Long usuarioId) {
        try {
            var cred = autenticacionClient.obtenerPorUsuarioId(usuarioId);
            String rol = (String) cred.get("rol");
            log.info(">>> Rol obtenido de ms-autenticacion para usuarioId={}: {}", usuarioId, rol);
            return rol;
        } catch (FeignException.NotFound e) {
            log.warn(">>> Usuario {} aun no tiene credenciales en ms-autenticacion", usuarioId);
            return "SIN_CREDENCIALES";
        } catch (FeignException e) {
            log.warn(">>> No se pudo conectar con ms-autenticacion: {}", e.getMessage());
            return "DESCONOCIDO";
        }
    }

    private UsuarioResponseDTO mapToDTO(Usuario u) {
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(u.getId());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setEmail(u.getEmail());
        dto.setTelefono(u.getTelefono());
        dto.setDireccion(u.getDireccion());
        dto.setActivo(u.getActivo());
        return dto;
    }

    public List<UsuarioResponseDTO> obtenerTodos() {
        log.info(">>> Obteniendo todos los usuarios");
        return usuarioRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public Optional<UsuarioResponseDTO> obtenerPorId(Long id) {
        log.info(">>> Buscando usuario id={}", id);
        return usuarioRepository.findById(id).map(u -> {
            UsuarioResponseDTO dto = mapToDTO(u);
            // Consulta ms-autenticacion via FeignClient para enriquecer la respuesta con el rol
            String rol = obtenerRolUsuario(id);
            dto.setRol(rol);
            return dto;
        });
    }

    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        log.info(">>> Creando usuario con email={}", dto.getEmail());
        if (usuarioRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("El email '" + dto.getEmail() + "' ya esta registrado.");
        }
        Usuario u = new Usuario();
        u.setNombre(dto.getNombre());
        u.setApellido(dto.getApellido());
        u.setEmail(dto.getEmail());
        u.setTelefono(dto.getTelefono());
        u.setDireccion(dto.getDireccion());
        Usuario guardado = usuarioRepository.save(u);
        log.info(">>> Usuario creado con id={}", guardado.getId());
        return mapToDTO(guardado);
    }

    public Optional<UsuarioResponseDTO> actualizar(Long id, UsuarioRequestDTO dto) {
        return usuarioRepository.findById(id).map(existente -> {
            log.info(">>> Actualizando usuario id={}", id);
            existente.setNombre(dto.getNombre());
            existente.setApellido(dto.getApellido());
            existente.setEmail(dto.getEmail());
            existente.setTelefono(dto.getTelefono());
            existente.setDireccion(dto.getDireccion());
            return mapToDTO(usuarioRepository.save(existente));
        });
    }

    public void eliminar(Long id) {
        log.info(">>> Eliminando usuario id={}", id);
        usuarioRepository.deleteById(id);
    }
}
