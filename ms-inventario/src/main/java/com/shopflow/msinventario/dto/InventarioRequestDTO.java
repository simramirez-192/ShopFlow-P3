package com.shopflow.msinventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventarioRequestDTO {

    @NotNull(message = "El productoId es obligatorio")
    private Long productoId;

    @NotNull(message = "El stock inicial es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    private Integer stockMinimo;

    private String ubicacion;

    // Campos para ajuste de stock (ENTRADA o SALIDA)
    // Se usan en PUT /inventario/producto/{id}/ajustar
    private String tipo;

    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    private Integer cantidad;

    private String motivo;
}
