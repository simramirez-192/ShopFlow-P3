package com.shopflow.mspago.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {

    private Long id;
    private Long ordenId;
    private BigDecimal monto;
    private String metodoPago;
    private String estado;
    private String referencia;
    private LocalDateTime fechaPago;
    private LocalDateTime creadoEn;
}
