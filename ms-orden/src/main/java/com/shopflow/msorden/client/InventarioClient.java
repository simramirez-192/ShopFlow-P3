package com.shopflow.msorden.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Descuenta stock en ms-inventario cuando se crea una orden
@FeignClient(name = "ms-inventario", url = "${ms.inventario.url}")
public interface InventarioClient {
    @PutMapping("/api/inventario/producto/{productoId}/ajustar")
    Map<String, Object> ajustarStock(@PathVariable Long productoId, @RequestBody Map<String, Object> body);
}
