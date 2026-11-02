package com.example.gestor.model;

public record ProyectoCambios(
        String nombre,
        String descripcion,
        Boolean activo,
        Integer numeroDeIncidencias
) {
}