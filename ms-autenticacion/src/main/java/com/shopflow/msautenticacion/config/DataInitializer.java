package com.shopflow.msautenticacion.config;

import com.shopflow.msautenticacion.model.Credencial;
import com.shopflow.msautenticacion.repository.CredencialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j @Component @RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CredencialRepository credencialRepository;

    @Override
    public void run(String... args) {
        if (credencialRepository.count() > 0) {
            log.info(">>> Credenciales ya cargadas. Se omite inicializacion.");
            return;
        }
        log.info(">>> Cargando credenciales iniciales...");
        credencialRepository.save(new Credencial(null, 1L, "admin",    "admin123",  "ADMIN",   true, null, null));
        credencialRepository.save(new Credencial(null, 2L, "juan123",  "pass1234",  "CLIENTE", true, null, null));
        credencialRepository.save(new Credencial(null, 3L, "maria456", "pass5678",  "CLIENTE", true, null, null));
        log.info(">>> ms-autenticacion listo en puerto 8080. Requiere ms-usuario en 8081.");
    }
}
