package com.shopflow.msenvio.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
// IMPORTANTE: Ver application.properties para el orden de arranque de los microservicios
@Slf4j @Component
public class DataInitializer implements CommandLineRunner {
    @Override public void run(String... args){ log.info(">>> ms-envio listo en puerto 8087. Requiere ms-orden en 8085."); }
}
