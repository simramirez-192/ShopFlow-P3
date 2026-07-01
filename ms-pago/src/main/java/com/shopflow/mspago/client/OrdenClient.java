package com.shopflow.mspago.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Valida que la orden exista y obtiene usuarioId desde ms-orden
@FeignClient(name = "ms-orden", url = "${ms.orden.url}")
public interface OrdenClient {
    @GetMapping("/api/ordenes/{id}")
    Map<String, Object> obtenerPorId(@PathVariable Long id);
}
