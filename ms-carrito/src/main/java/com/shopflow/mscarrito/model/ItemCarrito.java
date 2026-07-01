package com.shopflow.mscarrito.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "items_carrito")
public class ItemCarrito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrito_id", nullable = false)
    private Carrito carrito;
    @Column(name = "producto_id", nullable = false)
    private Long productoId;
    @Column(nullable = false)
    private Integer cantidad;
    @Column(name = "precio_unit", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnit;
}
