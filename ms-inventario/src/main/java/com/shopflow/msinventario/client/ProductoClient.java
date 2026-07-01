package com.shopflow.msinventario.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Valida que el producto exista en ms-producto
@FeignClient(name = "ms-producto", url = "${ms.producto.url}")
public interface ProductoClient {
    @GetMapping("/api/productos/{id}")
    Map<String, Object> obtenerPorId(@PathVariable Long id);
}
