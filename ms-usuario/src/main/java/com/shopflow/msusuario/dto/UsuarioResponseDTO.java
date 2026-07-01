package com.shopflow.msusuario.dto;

import lombok.Data;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String direccion;
    private Boolean activo;
    // Obtenido desde ms-autenticacion al consultar un usuario por id
    private String rol;
}
