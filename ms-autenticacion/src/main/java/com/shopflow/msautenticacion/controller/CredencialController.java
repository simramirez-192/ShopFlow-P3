package com.shopflow.msautenticacion.controller;

import com.shopflow.msautenticacion.dto.CredencialRequestDTO;
import com.shopflow.msautenticacion.dto.CredencialResponseDTO;
import com.shopflow.msautenticacion.service.CredencialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/autenticacion")
@RequiredArgsConstructor
public class CredencialController {

    private final CredencialService credencialService;

    @GetMapping
    public ResponseEntity<List<CredencialResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(credencialService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CredencialResponseDTO> obtenerPorId(@PathVariable Long id) {
        return credencialService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Endpoint para que ms-usuario consulte el rol de un usuario
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CredencialResponseDTO> obtenerPorUsuarioId(@PathVariable Long usuarioId) {
        return credencialService.obtenerPorUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/registro")
    public ResponseEntity<CredencialResponseDTO> crear(@Valid @RequestBody CredencialRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(credencialService.crear(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<CredencialResponseDTO> login(@RequestBody CredencialRequestDTO dto) {
        return credencialService.login(dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CredencialResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody CredencialRequestDTO dto) {
        return credencialService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        credencialService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
