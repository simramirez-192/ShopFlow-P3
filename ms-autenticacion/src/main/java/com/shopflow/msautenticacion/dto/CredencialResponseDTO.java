package com.shopflow.msautenticacion.dto;

import lombok.Data;

@Data
public class CredencialResponseDTO {

    private Long id;
    private Long usuarioId;
    private String username;
    private String rol;
    private Boolean activo;
}
