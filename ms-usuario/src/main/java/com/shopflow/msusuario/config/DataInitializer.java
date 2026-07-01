package com.shopflow.msusuario.config;

import com.shopflow.msusuario.model.Usuario;
import com.shopflow.msusuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            log.info(">>> Usuarios ya cargados. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando usuarios iniciales...");
        usuarioRepository.save(new Usuario(null, "Admin",     "ShopFlow", "admin@shopflow.com",  "+56912345678", "Calle Admin 1",      true, null, null));
        usuarioRepository.save(new Usuario(null, "Juan",      "Pérez",    "juan@email.com",      "+56987654321", "Av. Principal 123",  true, null, null));
        usuarioRepository.save(new Usuario(null, "María",     "González", "maria@email.com",     "+56911223344", "Pasaje Los Robles 5",true, null, null));
        log.info(">>> 3 usuarios cargados OK.");
    }
}
