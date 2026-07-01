package com.shopflow.msenvio.dto;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EnvioResponseDTO {

    private Long id;
    private Long ordenId;
    private String direccionDestino;
    private String estado;
    private String numeroSeguimiento;
    private String transportista;
    private LocalDate fechaEstimada;
    private LocalDateTime fechaEntrega;
    private LocalDateTime creadoEn;
}
