package com.shopflow.msproducto.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Registra stock inicial en ms-inventario cuando se crea un producto
@FeignClient(name = "ms-inventario", url = "${ms.inventario.url}")
public interface InventarioClient {
    @PostMapping("/api/inventario")
    Map<String, Object> crear(@RequestBody Map<String, Object> body);
}
