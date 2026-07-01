package com.shopflow.mspago.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "pagos")
public class Pago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "orden_id", nullable = false, unique = true)
    private Long ordenId;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;
    @Column(name = "metodo_pago", nullable = false, length = 50)
    private String metodoPago;
    @Column(nullable = false, length = 30)
    private String estado;
    @Column(length = 100)
    private String referencia;
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    @Column(name = "creado_en")
    private LocalDateTime creadoEn;
    @PrePersist protected void prePersist() {
        creadoEn = LocalDateTime.now();
        if (estado == null) estado = "PENDIENTE";
    }
}
