package com.shopflow.msorden.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "ordenes")
public class Orden {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(nullable = false, length = 30)
    private String estado;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;
    @Column(name = "direccion_envio", nullable = false, length = 255)
    private String direccionEnvio;
    @Column(columnDefinition = "TEXT")
    private String notas;
    @OneToMany(mappedBy = "orden", cascade = CascadeType.ALL)
    private List<ItemOrden> items = new ArrayList<>();
    @Column(name = "creado_en") private LocalDateTime creadoEn;
    @Column(name = "actualizado_en") private LocalDateTime actualizadoEn;
    @PrePersist protected void prePersist() {
        creadoEn = LocalDateTime.now(); actualizadoEn = LocalDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }
    @PreUpdate protected void preUpdate() { actualizadoEn = LocalDateTime.now(); }
}
