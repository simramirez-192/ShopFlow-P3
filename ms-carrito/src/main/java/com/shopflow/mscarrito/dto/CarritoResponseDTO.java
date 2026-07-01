package com.shopflow.mscarrito.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CarritoResponseDTO {

    private Long id;
    private Long usuarioId;
    private String estado;
    private List<Item> items;
    private BigDecimal total;

    // ── Clase interna: item dentro de la respuesta ───────────
    @Data
    public static class Item {
        private Long id;
        private Long productoId;
        private Integer cantidad;
        private BigDecimal precioUnit;
        private BigDecimal subtotal;
    }
}
