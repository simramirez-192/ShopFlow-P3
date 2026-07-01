package com.shopflow.msorden.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Valida que el usuario exista en ms-usuario antes de crear la orden
@FeignClient(name = "ms-usuario", url = "${ms.usuario.url}")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{id}")
    Map<String, Object> obtenerPorId(@PathVariable Long id);
}
