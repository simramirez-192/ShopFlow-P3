package com.shopflow.msorden.client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

// Envia notificacion ORDEN_CREADA al usuario via ms-notificacion
@FeignClient(name = "ms-notificacion", url = "${ms.notificacion.url}")
public interface NotificacionClient {
    @PostMapping("/api/notificaciones")
    Map<String, Object> crear(@RequestBody Map<String, Object> body);
}
