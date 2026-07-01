package com.shopflow.msinventario.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "movimientos_stock")
public class MovimientoStock {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    @Column(nullable = false, length = 20)
    private String tipo;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(length = 255)
    private String motivo;
    private LocalDateTime fecha;
    @PrePersist
    protected void prePersist() { fecha = LocalDateTime.now(); }
}
