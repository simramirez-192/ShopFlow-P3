package com.shopflow.msinventario.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity @Table(name = "inventario")
public class Inventario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId;
    @Column(nullable = false)
    private Integer stock;
    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;
    @Column(length = 100)
    private String ubicacion;
    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;
    @PrePersist @PreUpdate
    protected void preUpdate() { actualizadoEn = LocalDateTime.now(); }
}
