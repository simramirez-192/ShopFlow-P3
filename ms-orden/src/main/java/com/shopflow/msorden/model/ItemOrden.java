package com.shopflow.msorden.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "items_orden")
public class ItemOrden {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orden_id", nullable = false)
    private Orden orden;
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    @Column(name = "nombre_prod", nullable = false, length = 150)
    private String nombreProducto;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;
}
