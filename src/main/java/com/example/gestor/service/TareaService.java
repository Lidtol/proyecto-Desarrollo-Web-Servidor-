package com.example.gestor.service;

import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.model.Tarea;
import com.example.gestor.repository.TareaRepository;

import java.util.ArrayList;
import java.util.List;

public class TareaService {

    private final TareaRepository repositorio;

    public TareaService(TareaRepository repositorio) {
        this.repositorio = repositorio;
    }

    /** completada == null significa "sin filtrar". */
    public List<Tarea> listar(Boolean completada) {
        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : repositorio.findAll()) {
            if (completada == null || tarea.isCompletada() == completada) {
                seleccion.add(tarea);
            }
        }
        return seleccion;
    }

    public Tarea obtener(int id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("tarea", id));
    }

    public Tarea crear(Tarea tarea) {
        return repositorio.save(tarea);
    }

    public Tarea reemplazar(int id, Tarea datos) {
        Tarea existente = obtener(id);
        existente.setTitulo(datos.getTitulo());
        existente.setPrioridad(datos.getPrioridad());
        existente.setProyectoId(datos.getProyectoId());
        existente.setCompletada(datos.isCompletada());
        return existente;
    }

    /** PATCH: un parámetro null significa "no tocar ese campo". Sin DTO: el service no conoce el contrato web. */
    public Tarea modificar(int id, String titulo, String prioridad, Integer proyectoId, Boolean completada) {
        Tarea existente = obtener(id);
        if (titulo != null) {
            existente.setTitulo(titulo);
        }
        if (prioridad != null) {
            existente.setPrioridad(prioridad);
        }
        if (proyectoId != null) {
            existente.setProyectoId(proyectoId);
        }
        if (completada != null) {
            existente.setCompletada(completada);
        }
        return existente;
    }

    /** Idempotente: borrar algo que no existe no es error (el contrato actual responde 204). */
    public void eliminar(int id) {
        repositorio.deleteById(id);
    }
}