package com.example.gestor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TareaRequest(
                @NotBlank(message = "El título es obligatorio") @Size(min = 3, max = 120, message = "El título debe tener entre 3 y 120 caracteres") String titulo,

                @NotNull @Pattern(regexp = "baja|media|alta") 
                @NotNull(message = "La prioridad es obligatoria")
                @PrioridadValida
                String prioridad,


                @NotNull @Positive Integer proyectoId,

                Boolean completada) {
}