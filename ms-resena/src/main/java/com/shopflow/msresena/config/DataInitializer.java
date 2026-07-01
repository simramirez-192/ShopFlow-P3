package com.shopflow.msresena.config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
// IMPORTANTE: Ver application.properties para el orden de arranque de los microservicios
@Slf4j @Component
public class DataInitializer implements CommandLineRunner {
    @Override public void run(String... args){ log.info(">>> ms-resena listo en puerto 8089. Requiere ms-producto en 8082 y ms-orden en 8085."); }
}
