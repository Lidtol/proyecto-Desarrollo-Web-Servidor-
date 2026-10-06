package com.example.gestor.dto;

import jakarta.validation.constraints.Size;

public record ProyectoPatchRequest(
        @Size(min = 3, max = 80)
        String nombre,

        @Size(max = 500)
        String descripcion,

        Boolean activo,

        Integer numeroDeIncidencias
) {
}