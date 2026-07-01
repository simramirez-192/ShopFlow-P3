package com.shopflow.msautenticacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CredencialRequestDTO {

    // Registro: requiere usuarioId + username + password
    // Login:    solo requiere username + password (usuarioId queda null)
    @NotNull(message = "El usuarioId es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El username no puede estar vacío")
    @Size(min = 4, max = 100, message = "El username debe tener entre 4 y 100 caracteres")
    private String username;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 4, max = 100, message = "La contraseña debe tener entre 4 y 100 caracteres")
    private String password;

    private String rol;
}
