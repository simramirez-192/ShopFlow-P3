package com.shopflow.mspago.controller;
import com.shopflow.mspago.dto.PagoRequestDTO;
import com.shopflow.mspago.dto.PagoResponseDTO;
import com.shopflow.mspago.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {
    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<List<PagoResponseDTO>> obtenerTodos() { return ResponseEntity.ok(pagoService.obtenerTodos()); }
    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<PagoResponseDTO> obtenerPorOrden(@PathVariable Long ordenId) {
        return pagoService.obtenerPorOrden(ordenId).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public ResponseEntity<PagoResponseDTO> procesar(@Valid @RequestBody PagoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pagoService.procesar(dto));
    }
}
