package com.shopflow.msenvio.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Obtiene datos de la orden (incluye usuarioId) desde ms-orden
@FeignClient(name = "ms-orden", url = "${ms.orden.url}")
public interface OrdenClient {
    @GetMapping("/api/ordenes/{id}")
    Map<String, Object> obtenerPorId(@PathVariable Long id);
}
