package com.shopflow.msproducto.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private String imagenUrl;
    private Boolean activo;
    private Long categoriaId;
    private String categoriaNombre;
    private LocalDateTime creadoEn;
}
