package com.shopflow.msenvio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// @EnableFeignClients → escanea el paquete buscando interfaces @FeignClient
// SIN esta anotacion → NoSuchBeanDefinitionException al inyectar el client
@SpringBootApplication
@EnableFeignClients
public class MsEnvioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsEnvioApplication.class, args);
    }
}
