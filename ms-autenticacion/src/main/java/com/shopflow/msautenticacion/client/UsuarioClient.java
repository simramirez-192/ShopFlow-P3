package com.shopflow.msautenticacion.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Llama a ms-usuario para verificar que el usuario existe al crear credencial
@FeignClient(name = "ms-usuario", url = "${ms.usuario.url}")
public interface UsuarioClient {
    @GetMapping("/api/usuarios/{id}")
    Map<String, Object> obtenerPorId(@PathVariable Long id);
}
