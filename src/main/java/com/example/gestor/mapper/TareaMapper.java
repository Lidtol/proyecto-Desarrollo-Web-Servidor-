package com.example.gestor.mapper;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.dto.TareaRequest;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.model.TareaCambios;
import com.example.gestor.model.Tarea;

import java.util.ArrayList;
import java.util.List;

/** Único sitio que traduce entre TareaRequest / TareaResponse y Tarea. */
public final class TareaMapper {

    private TareaMapper() {
    }

    /** Crea una Tarea a partir de una entrada de alta o sustitución (sin id). */
    public static Tarea aModelo(TareaRequest peticion) {
        Tarea tarea = new Tarea();
        tarea.setTitulo(peticion.titulo());
        tarea.setPrioridad(peticion.prioridad());
        tarea.setProyectoId(peticion.proyectoId());
        tarea.setCompletada(Boolean.TRUE.equals(peticion.completada()));
        return tarea;
    }

    public static TareaCambios aCambios(TareaPatchRequest peticion) {
        return new TareaCambios(
                peticion.titulo(),
                peticion.prioridad(),
                peticion.proyectoId(),
                peticion.completada());
    }

    public static TareaResponse aRespuesta(Tarea tarea) {
        return new TareaResponse(
                tarea.getId(),
                tarea.getTitulo(),
                tarea.getPrioridad(),
                tarea.getProyectoId(),
                tarea.isCompletada());
    }

    public static List<TareaResponse> aRespuestas(List<Tarea> tareas) {
        List<TareaResponse> resultado = new ArrayList<>();
        for (Tarea tarea : tareas) {
            resultado.add(aRespuesta(tarea));
        }
        return resultado;
    }
}
