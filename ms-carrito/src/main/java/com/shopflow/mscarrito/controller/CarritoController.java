package com.shopflow.mscarrito.controller;
import com.shopflow.mscarrito.dto.CarritoRequestDTO;
import com.shopflow.mscarrito.dto.CarritoResponseDTO.Item;
import com.shopflow.mscarrito.dto.CarritoResponseDTO;
import com.shopflow.mscarrito.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
public class CarritoController {
    private final CarritoService carritoService;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<CarritoResponseDTO> obtenerCarrito(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(carritoService.obtenerOCrearCarrito(usuarioId));
    }
    @PostMapping("/usuario/{usuarioId}/items")
    public ResponseEntity<CarritoResponseDTO> agregarItem(
            @PathVariable Long usuarioId, @Valid @RequestBody CarritoRequestDTO dto) {
        return ResponseEntity.ok(carritoService.agregarItem(usuarioId, dto));
    }
    @DeleteMapping("/usuario/{usuarioId}/items/{itemId}")
    public ResponseEntity<CarritoResponseDTO> eliminarItem(
            @PathVariable Long usuarioId, @PathVariable Long itemId) {
        return ResponseEntity.ok(carritoService.eliminarItem(usuarioId, itemId));
    }
    @DeleteMapping("/usuario/{usuarioId}")
    public ResponseEntity<Void> vaciarCarrito(@PathVariable Long usuarioId) {
        carritoService.vaciarCarrito(usuarioId);
        return ResponseEntity.noContent().build();
    }
}
