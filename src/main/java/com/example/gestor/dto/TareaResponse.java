package com.example.gestor.dto;


import com.example.gestor.model.Tarea;


public record TareaResponse(
        int id,
        String titulo,
        String prioridad,
        boolean completada
) {

    public static TareaResponse desde(Tarea tarea) {
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getPrioridad(),
                tarea.isCompletada()
        );
    }
}