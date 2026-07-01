package com.shopflow.msresena.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "resenas")
public class Resena {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(name = "orden_id", nullable = false)
    private Long ordenId;
    @Column(nullable = false)
    private Integer calificacion;
    @Column(columnDefinition = "TEXT")
    private String comentario;
    @Column(nullable = false)
    private Boolean activa;
    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
    @PrePersist protected void prePersist() {
        creadoEn = LocalDateTime.now();
        if (activa == null) activa = true;
    }
}
