package com.shopflow.mscarrito.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Obtiene precio y verifica existencia del producto en ms-producto
@FeignClient(name = "ms-producto", url = "${ms.producto.url}")
public interface ProductoClient {
    @GetMapping("/api/productos/{id}")
    Map<String, Object> obtenerPorId(@PathVariable Long id);
}
