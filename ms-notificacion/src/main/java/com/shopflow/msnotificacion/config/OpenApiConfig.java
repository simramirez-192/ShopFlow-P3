package com.shopflow.msnotificacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación Swagger/OpenAPI para el microservicio Notificación.
 * Disponible en: /swagger-ui/index.html  y  /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI notificacionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShopFlow - Microservicio de Notificación")
                        .description("Gestiona el envío de notificaciones a los usuarios.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo ShopFlow")
                                .email("shopflow@duocuc.cl")));
    }
}
