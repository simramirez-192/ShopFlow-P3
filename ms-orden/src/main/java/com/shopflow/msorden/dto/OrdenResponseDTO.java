package com.shopflow.msorden.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdenResponseDTO {

    private Long id;
    private Long usuarioId;
    private String estado;
    private BigDecimal total;
    private String direccionEnvio;
    private String notas;
    private LocalDateTime creadoEn;
    private List<Item> items;

    // ── Clase interna: item dentro de la respuesta ───────────
    @Data
    public static class Item {
        private Long id;
        private Long productoId;
        private String nombreProducto;
        private Integer cantidad;
        private BigDecimal precioUnit;
        private BigDecimal subtotal;
    }
}
