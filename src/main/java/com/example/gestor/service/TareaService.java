package com.example.gestor.service;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.error.ReglaDeNegocioException;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;
import com.example.gestor.repository.ProyectoRepository;
import com.example.gestor.repository.TareaRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository repositorio;
    private final ProyectoRepository proyectos;

    public TareaService(TareaRepository repositorio, ProyectoRepository proyectos) {
        this.repositorio = repositorio;
        this.proyectos = proyectos;
    }

    public List<Tarea> listar(Boolean completada) {
        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : repositorio.findAll()) {
            if (completada == null || tarea.isCompletada() == completada) {
                seleccion.add(tarea);
            }
        }
        return seleccion;
    }

    public Tarea buscar(int id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("tarea", id));
    }

    public Tarea crear(Tarea tarea) {
        comprobarQueElProyectoExiste(tarea.getProyectoId());
        comprobarQueElProyectoAdmiteTareas(tarea.getProyectoId());
        tarea.setCompletada(false);
        return repositorio.save(tarea);
    }

    public Tarea sustituir(int id, Tarea nueva) {
        Tarea actual = buscar(id);
        comprobarElProyectoDeLaTarea(nueva.getProyectoId(), actual.getProyectoId());
        nueva.setId(id);
        return repositorio.save(nueva);
    }

    public Tarea modificar(int id, TareaPatchRequest cambios) {
        Tarea tarea = buscar(id);
        if (cambios.proyectoId() != null) {
            comprobarElProyectoDeLaTarea(cambios.proyectoId(), tarea.getProyectoId());
        }
        TareaMapper.aplicarCambios(tarea, cambios);
        return repositorio.save(tarea);
    }

    public void eliminar(int id) {
        repositorio.deleteById(id);
    }

    /**
     * Decisión: una tarea que ya vive en un proyecto archivado puede seguir editándose
     * (p. ej. completarse). Lo que se prohíbe es MOVER una tarea a un proyecto que no existe
     * o que no admite tareas.
     */
    private void comprobarElProyectoDeLaTarea(int proyectoNuevo, int proyectoActual) {
        comprobarQueElProyectoExiste(proyectoNuevo);
        if (proyectoNuevo != proyectoActual) {
            comprobarQueElProyectoAdmiteTareas(proyectoNuevo);
        }
    }

    private void comprobarQueElProyectoExiste(int proyectoId) {
        if (!proyectos.existsById(proyectoId)) {
            throw new ReglaDeNegocioException("No existe el proyecto " + proyectoId);
        }
    }

    private void comprobarQueElProyectoAdmiteTareas(int proyectoId) {
        Proyecto proyecto = proyectos.findById(proyectoId).orElseThrow();
        if (!proyecto.isActivo()) {
            throw new ReglaDeNegocioException(
                    "El proyecto " + proyectoId + " está archivado y no admite tareas");
        }
    }
}
