package com.example.gestor.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProyectoPatchRequest(
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        @Pattern(regexp = "(?s).*\\S.*", message = "El nombre no puede estar en blanco")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        Boolean activo,

        @PositiveOrZero(message = "El número de incidencias debe ser 0 o mayor")
        Integer numeroDeIncidencias
) {
    public ProyectoPatchRequest {
        nombre = nombre == null ? null : nombre.trim();
    }
}
