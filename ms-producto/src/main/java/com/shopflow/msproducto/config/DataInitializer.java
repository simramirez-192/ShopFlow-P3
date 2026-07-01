package com.shopflow.msproducto.config;
import com.shopflow.msproducto.model.Categoria;
import com.shopflow.msproducto.model.Producto;
import com.shopflow.msproducto.repository.CategoriaRepository;
import com.shopflow.msproducto.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Override
    public void run(String... args) {
        if (productoRepository.count() > 0) {
            log.info(">>> Productos ya cargados. Se omite inicialización.");
            return;
        }
        log.info(">>> Cargando productos iniciales...");
        Categoria electronica = categoriaRepository.findByNombre("Electrónica")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Electrónica", "Dispositivos electrónicos", true, null)));
        Categoria ropa = categoriaRepository.findByNombre("Ropa")
                .orElseGet(() -> categoriaRepository.save(new Categoria(null, "Ropa", "Prendas de vestir", true, null)));

        productoRepository.save(new Producto(null, "Smartphone Samsung A54", "Teléfono 128GB", new BigDecimal("399990"), null, true, electronica, null, null));
        productoRepository.save(new Producto(null, "Audífonos Bluetooth",    "Audífonos inalámbricos", new BigDecimal("49990"), null, true, electronica, null, null));
        productoRepository.save(new Producto(null, "Polera Básica",          "100% algodón talla M",   new BigDecimal("9990"),  null, true, ropa,       null, null));
        log.info(">>> 3 productos cargados OK.");
    }
}
