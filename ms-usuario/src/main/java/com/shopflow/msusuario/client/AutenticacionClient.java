package com.shopflow.msusuario.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Llama a ms-autenticacion para obtener el rol del usuario
@FeignClient(name = "ms-autenticacion", url = "${ms.autenticacion.url}")
public interface AutenticacionClient {
    @GetMapping("/api/autenticacion/usuario/{usuarioId}")
    Map<String, Object> obtenerPorUsuarioId(@PathVariable Long usuarioId);
}
