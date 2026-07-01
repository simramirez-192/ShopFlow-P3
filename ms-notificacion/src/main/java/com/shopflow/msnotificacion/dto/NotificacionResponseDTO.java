package com.shopflow.msnotificacion.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificacionResponseDTO {

    private Long id;
    private Long usuarioId;
    private String tipo;
    private String titulo;
    private String mensaje;
    private Boolean leida;
    private Boolean enviada;
    private LocalDateTime creadoEn;
}
