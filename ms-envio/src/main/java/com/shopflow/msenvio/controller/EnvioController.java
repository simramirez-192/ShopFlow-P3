package com.shopflow.msenvio.controller;
import com.shopflow.msenvio.dto.EnvioRequestDTO;
import com.shopflow.msenvio.dto.EnvioResponseDTO;
import com.shopflow.msenvio.service.EnvioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/envios")
@RequiredArgsConstructor
public class EnvioController {
    private final EnvioService envioService;

    @GetMapping
    public ResponseEntity<List<EnvioResponseDTO>> obtenerTodos() { return ResponseEntity.ok(envioService.obtenerTodos()); }
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<EnvioResponseDTO> obtenerPorOrden(@PathVariable Long ordenId) {
        return envioService.obtenerPorOrden(ordenId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<EnvioResponseDTO> crear(@Valid @RequestBody EnvioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(envioService.crear(dto));
    }
    @PutMapping("/{id}/estado")
    public ResponseEntity<EnvioResponseDTO> actualizarEstado(@PathVariable Long id, @Valid @RequestBody EnvioRequestDTO dto) {
        return envioService.actualizarEstado(id, dto).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
}
