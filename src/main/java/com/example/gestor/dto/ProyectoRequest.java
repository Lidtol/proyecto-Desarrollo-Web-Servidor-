package com.example.gestor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProyectoRequest(
        @NotBlank
        @Size(min = 3, max = 80)
        String nombre,

        @Size(max = 500)
        String descripcion,

        boolean activo,

        int numeroDeIncidencias
) {
}