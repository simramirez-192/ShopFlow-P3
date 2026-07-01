package com.shopflow.msresena.controller;
import com.shopflow.msresena.dto.ResenaRequestDTO;
import com.shopflow.msresena.dto.ResenaResponseDTO;
import com.shopflow.msresena.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/resenas")
@RequiredArgsConstructor
public class ResenaController {
    private final ResenaService resenaService;

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPorProducto(productoId));
    }
    @GetMapping("/producto/{productoId}/promedio")
    public ResponseEntity<ResenaResponseDTO> obtenerPromedio(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.obtenerPromedio(productoId));
    }
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ResenaResponseDTO>> obtenerPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.obtenerPorUsuario(usuarioId));
    }
    @PostMapping
    public ResponseEntity<ResenaResponseDTO> crear(@Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resenaService.crear(dto));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
