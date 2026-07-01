package com.shopflow.msorden.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrdenRequestDTO {

    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "La dirección de envío es obligatoria")
    private String direccionEnvio;

    private String notas;

    @NotNull(message = "La lista de items es obligatoria")
    private List<Item> items;

    // Campo para cambiar estado: se usa en PUT /ordenes/{id}/estado
    private String estado;

    // ── Clase interna: item de la orden ──────────────────────
    @Data
    public static class Item {

        @NotNull(message = "El productoId del item es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad del item es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;
    }
}
