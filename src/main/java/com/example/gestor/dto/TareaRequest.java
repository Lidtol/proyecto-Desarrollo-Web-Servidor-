package com.example.gestor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record TareaRequest(
        @NotBlank @Size(min = 3, max = 120) String titulo,

        @NotNull @Pattern(regexp = "baja|media|alta") String prioridad,

        @NotNull @Positive Integer proyectoId,

        Boolean completada) {
}