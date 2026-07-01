package com.shopflow.msnotificacion.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "notificaciones")
public class Notificacion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;
    @Column(nullable = false, length = 50)
    private String tipo;
    @Column(nullable = false, length = 200)
    private String titulo;
    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensaje;
    @Column(nullable = false)
    private Boolean leida;
    @Column(nullable = false)
    private Boolean enviada;
    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
    @PrePersist protected void prePersist() {
        creadoEn = LocalDateTime.now();
        if (leida == null) leida = false;
        if (enviada == null) enviada = false;
    }
}
