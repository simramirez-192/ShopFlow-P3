package com.shopflow.mscarrito.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "carritos")
public class Carrito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false, unique = true)
    private Long usuarioId;
    @Column(nullable = false, length = 20)
    private String estado;
    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemCarrito> items = new ArrayList<>();
    @Column(name = "creado_en") private LocalDateTime creadoEn;
    @Column(name = "actualizado_en") private LocalDateTime actualizadoEn;
    @PrePersist protected void prePersist() {
        creadoEn = LocalDateTime.now(); actualizadoEn = LocalDateTime.now();
        if (estado == null) estado = "ACTIVO";
    }
    @PreUpdate protected void preUpdate() { actualizadoEn = LocalDateTime.now(); }
}
