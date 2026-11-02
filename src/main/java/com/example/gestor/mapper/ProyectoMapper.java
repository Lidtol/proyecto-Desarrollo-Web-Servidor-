package com.example.gestor.mapper;

import com.example.gestor.dto.ProyectoPatchRequest;
import com.example.gestor.dto.ProyectoRequest;
import com.example.gestor.dto.ProyectoResponse;
import com.example.gestor.model.ProyectoCambios;
import com.example.gestor.model.Proyecto;

import java.util.ArrayList;
import java.util.List;

/** Único sitio que traduce entre ProyectoRequest / ProyectoResponse y Proyecto. */
public final class ProyectoMapper {

    private ProyectoMapper() {
    }

    /** Crea un Proyecto a partir de una entrada de alta o sustitución (sin id). */
    public static Proyecto aModelo(ProyectoRequest peticion) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(peticion.nombre() == null ? null : peticion.nombre().trim());
        proyecto.setDescripcion(peticion.descripcion());
        proyecto.setActivo(Boolean.TRUE.equals(peticion.activo()));
        proyecto.setNumeroDeIncidencias(
                peticion.numeroDeIncidencias() != null ? peticion.numeroDeIncidencias() : 0);
        proyecto.setFechaInicio(peticion.fechaInicio());
        proyecto.setFechaFin(peticion.fechaFin());
        return proyecto;
    }

    public static ProyectoCambios aCambios(ProyectoPatchRequest peticion) {
        return new ProyectoCambios(
                peticion.nombre(),
                peticion.descripcion(),
                peticion.activo(),
                peticion.numeroDeIncidencias());
    }

    public static ProyectoResponse aRespuesta(Proyecto proyecto) {
        return new ProyectoResponse(
                proyecto.getId(),
                proyecto.getNombre(),
                proyecto.getDescripcion(),
                proyecto.isActivo(),
                proyecto.getNumeroDeIncidencias(),
                proyecto.getFechaInicio(),
                proyecto.getFechaFin());
    }

    public static List<ProyectoResponse> aRespuestas(List<Proyecto> proyectos) {
        List<ProyectoResponse> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos) {
            resultado.add(aRespuesta(proyecto));
        }
        return resultado;
    }
}
