package com.shopflow.msinventario.controller;
import com.shopflow.msinventario.dto.InventarioRequestDTO;
import com.shopflow.msinventario.dto.InventarioResponseDTO;
import com.shopflow.msinventario.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {
    private final InventarioService inventarioService;

    @GetMapping
    public ResponseEntity<List<InventarioResponseDTO>> obtenerTodos() {
        return ResponseEntity.ok(inventarioService.obtenerTodos());
    }
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<InventarioResponseDTO> obtenerPorProducto(@PathVariable Long productoId) {
        return inventarioService.obtenerPorProducto(productoId)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<InventarioResponseDTO> crear(@Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventarioService.crear(dto));
    }
    @PutMapping("/producto/{productoId}/ajustar")
    public ResponseEntity<InventarioResponseDTO> ajustar(
            @PathVariable Long productoId, @Valid @RequestBody InventarioRequestDTO dto) {
        return ResponseEntity.ok(inventarioService.ajustarStock(productoId, dto));
    }
}
