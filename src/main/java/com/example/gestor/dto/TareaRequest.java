package com.example.gestor.dto;

import com.example.gestor.validacion.PrioridadValida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TareaRequest(
        @NotBlank(message = "El título es obligatorio: escribe un título para la tarea")
        @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres")
        String titulo,

        @NotNull(message = "La prioridad es obligatoria: usa baja, media o alta")
        @PrioridadValida
        String prioridad,

        @NotNull(message = "El proyecto es obligatorio: indica el id de un proyecto")
        @Positive(message = "El id del proyecto debe ser un número mayor que 0")
        Integer proyectoId,

        Boolean completada
) {
    public TareaRequest {
        titulo = titulo == null ? null : titulo.trim();
    }
}
