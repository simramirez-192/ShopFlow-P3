package com.shopflow.mspago.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de la documentación Swagger/OpenAPI para el microservicio Pago.
 * Disponible en: /swagger-ui/index.html  y  /v3/api-docs
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI pagoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShopFlow - Microservicio de Pago")
                        .description("Gestiona el procesamiento de pagos asociados a las órdenes.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo ShopFlow")
                                .email("shopflow@duocuc.cl")));
    }
}
