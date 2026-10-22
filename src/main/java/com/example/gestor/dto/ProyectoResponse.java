package com.example.gestor.dto;

import java.time.LocalDate;

public record ProyectoResponse(
        int id,
        String nombre,
        String descripcion,
        boolean activo,
        int numeroDeIncidencias,
        LocalDate fechaInicio,
        LocalDate fechaFin
) {
}
