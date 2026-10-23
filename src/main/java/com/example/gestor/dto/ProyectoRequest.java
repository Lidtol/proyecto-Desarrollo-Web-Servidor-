package com.example.gestor.dto;

import com.example.gestor.validacion.FechasCoherentes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

@FechasCoherentes
public record ProyectoRequest(
        @NotBlank(message = "El nombre es obligatorio: escribe un nombre para el proyecto")
        @Size(min = 3, max = 80, message = "El nombre debe tener entre 3 y 80 caracteres")
        String nombre,

        @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
        String descripcion,

        Boolean activo,

        @PositiveOrZero(message = "El número de incidencias debe ser 0 o mayor")
        Integer numeroDeIncidencias,

        LocalDate fechaInicio,
        LocalDate fechaFin
) {
    /** Se recorta antes de validar: "  ab  " no puede pasar @Size y guardarse como "ab". */
    public ProyectoRequest {
        nombre = nombre == null ? null : nombre.trim();
    }
}
