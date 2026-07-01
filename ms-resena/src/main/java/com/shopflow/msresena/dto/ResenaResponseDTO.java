package com.shopflow.msresena.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ResenaResponseDTO {

    private Long id;
    private Long productoId;
    private Long usuarioId;
    private Long ordenId;
    private Integer calificacion;
    private String comentario;
    private Boolean activa;
    private LocalDateTime creadoEn;

    // Campos de promedio: se usan en GET /resenas/producto/{id}/promedio
    private Double promedioCalificacion;
    private Long totalResenas;
}
