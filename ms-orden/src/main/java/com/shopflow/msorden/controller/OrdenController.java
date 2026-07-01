package com.shopflow.msorden.controller;
import com.shopflow.msorden.dto.OrdenRequestDTO;
import com.shopflow.msorden.dto.OrdenResponseDTO;
import com.shopflow.msorden.service.OrdenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
@RequiredArgsConstructor
public class OrdenController {
    private final OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> obtenerTodas() {
        return ResponseEntity.ok(ordenService.obtenerTodas());
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ordenService.obtenerPorId(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<OrdenResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenService.obtenerPorUsuario(usuarioId));
    }
    @PostMapping
    public ResponseEntity<OrdenResponseDTO> crear(@Valid @RequestBody OrdenRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordenService.crear(dto));
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<OrdenResponseDTO> actualizarEstado(
            @PathVariable Long id, @Valid @RequestBody OrdenRequestDTO dto) {
        return ordenService.actualizarEstado(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
