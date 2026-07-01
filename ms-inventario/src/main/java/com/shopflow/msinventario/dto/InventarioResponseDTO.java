package com.shopflow.msinventario.dto;

import lombok.Data;

@Data
public class InventarioResponseDTO {

    private Long id;
    private Long productoId;
    private Integer stock;
    private Integer stockMinimo;
    private String ubicacion;
    private Boolean stockBajo;
}
