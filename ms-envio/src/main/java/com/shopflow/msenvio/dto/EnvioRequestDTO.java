package com.shopflow.msenvio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EnvioRequestDTO {

    @NotNull(message = "El ordenId es obligatorio")
    private Long ordenId;

    @NotBlank(message = "La dirección de destino es obligatoria")
    private String direccionDestino;

    private String transportista;

    // Campo para actualizar estado: se usa en PUT /envios/{id}/estado
    private String estado;
}
