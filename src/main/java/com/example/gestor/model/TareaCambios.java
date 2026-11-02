package com.example.gestor.model;

public record TareaCambios(
        String titulo,
        String prioridad,
        Integer proyectoId,
        Boolean completada
) {
}