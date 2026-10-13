package com.example.gestor.dto;

import java.time.LocalDate;

import com.example.gestor.validacion.FechasCoherentes;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@FechasCoherentes
public record ProyectoRequest(
        @NotBlank
        @Size(min = 3, max = 80,
                message = " Nombre obligatorio y de entre 3 y 80 carácteres "
        )
        String nombre,

        @Size(max = 500,
                message = " Descripcion máxima de 500 carácteres."
        )
        String descripcion,

        Boolean activo,
        Integer numeroDeIncidencias,
        LocalDate fechaInicio,
        LocalDate fechaFin

) {

    public Object getFechaFin() {
        return fechaFin;
    }

    // getters y setters si es necesario, aunque los records ya generan automáticamente los métodos de acceso.

    public Object getFechaInicio() {
        return fechaInicio;
    }


    public Object setFechaInicio(LocalDate fechaInicio) {
        return fechaInicio;
    }

    public Object setFechaFin(LocalDate fechaFin) {
        return fechaFin;
    }

}