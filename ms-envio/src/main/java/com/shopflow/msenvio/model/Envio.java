package com.shopflow.msenvio.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "envios")
public class Envio {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_id", nullable = false, unique = true)
    private Long ordenId;
    @Column(name = "direccion_destino", nullable = false, length = 255)
    private String direccionDestino;
    @Column(nullable = false, length = 30)
    private String estado;
    @Column(name = "numero_seguimiento", length = 100)
    private String numeroSeguimiento;
    @Column(length = 100)
    private String transportista;
    @Column(name = "fecha_estimada")
    private LocalDate fechaEstimada;
    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;
    @Column(name = "creado_en") private LocalDateTime creadoEn;
    @Column(name = "actualizado_en") private LocalDateTime actualizadoEn;
    @PrePersist protected void prePersist() {
        creadoEn = LocalDateTime.now(); actualizadoEn = LocalDateTime.now();
        if (estado == null) estado = "PREPARANDO";
    }
    @PreUpdate protected void preUpdate() { actualizadoEn = LocalDateTime.now(); }
}
