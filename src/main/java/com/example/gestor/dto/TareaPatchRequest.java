package com.example.gestor.dto;

import com.example.gestor.validacion.PrioridadValida;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TareaPatchRequest(
        @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
        @Pattern(regexp = "(?s).*\\S.*", message = "El título no puede estar en blanco")
        String titulo,

        @PrioridadValida
        String prioridad,

        @Positive(message = "El id del proyecto debe ser un número mayor que 0")
        Integer proyectoId,

        Boolean completada
) {
    public TareaPatchRequest {
        titulo = titulo == null ? null : titulo.trim();
    }
}
