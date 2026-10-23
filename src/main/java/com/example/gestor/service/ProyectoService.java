package com.example.gestor.service;

import com.example.gestor.error.ConflictoException;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;
import com.example.gestor.repository.ProyectoRepository;
import com.example.gestor.repository.TareaRepository;

import java.util.ArrayList;
import java.util.List;

public class ProyectoService {

    private final ProyectoRepository repositorio;
    // Solo lectura: se usa para GET /proyectos/{id}/tareas. Las altas de tareas
    // pasan siempre por TareaService, así no hay dos contadores de ids en juego.
    private final TareaRepository repositorioTareas;

    public ProyectoService(ProyectoRepository repositorio, TareaRepository repositorioTareas) {
        this.repositorio = repositorio;
        this.repositorioTareas = repositorioTareas;
    }

    /** activo == null significa "sin filtrar". */
    public List<Proyecto> listar(Boolean activo) {
        List<Proyecto> seleccion = new ArrayList<>();
        for (Proyecto proyecto : repositorio.findAll()) {
            if (activo == null || proyecto.isActivo() == activo) {
                seleccion.add(proyecto);
            }
        }
        return seleccion;
    }

    public Proyecto obtener(int id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("proyecto", id));
    }

    public Proyecto crear(Proyecto proyecto) {
        comprobarNombreLibre(proyecto.getNombre(), null);
        return repositorio.save(proyecto);
    }

    public Proyecto reemplazar(int id, Proyecto datos) {
        Proyecto existente = obtener(id);               // primero 404...
        comprobarNombreLibre(datos.getNombre(), id);    // ...después 409, como en la UD3
        existente.setNombre(datos.getNombre());
        existente.setDescripcion(datos.getDescripcion());
        existente.setActivo(datos.isActivo());
        existente.setNumeroDeIncidencias(datos.getNumeroDeIncidencias());
        existente.setFechaInicio(datos.getFechaInicio());
        existente.setFechaFin(datos.getFechaFin());
        return existente;
    }

    /** PATCH: un parámetro null significa "no tocar ese campo". */
    public Proyecto modificar(int id, String nombre, String descripcion,
                              Boolean activo, Integer numeroDeIncidencias) {
        Proyecto existente = obtener(id);
        if (nombre != null) {
            comprobarNombreLibre(nombre, id);
            existente.setNombre(nombre);
        }
        if (descripcion != null) {
            existente.setDescripcion(descripcion);
        }
        if (activo != null) {
            existente.setActivo(activo);
        }
        if (numeroDeIncidencias != null) {
            existente.setNumeroDeIncidencias(numeroDeIncidencias);
        }
        return existente;
    }

    public void eliminar(int id) {
        repositorio.deleteById(id);
    }

    /** 404 si el proyecto no existe; lista vacía (200) si existe y no tiene tareas. */
    public List<Tarea> tareasDelProyecto(int id) {
        obtener(id);
        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : repositorioTareas.findAll()) {
            if (tarea.getProyectoId() == id) {
                seleccion.add(tarea);
            }
        }
        return seleccion;
    }

    /**
     * Esto es una regla de negocio y no una validación de formato: para decidirlo
     * hay que mirar los OTROS proyectos que ya existen (el estado de la aplicación),
     * mientras que una validación de formato (@Size, @NotBlank...) solo mira la
     * petición recibida y se puede comprobar sin saber nada más del sistema.
     * Si mañana los datos viven en una base de datos, esta comprobación seguirá
     * siendo una decisión del dominio y no del formato del JSON.
     *
     * idPropio: el proyecto que se está modificando (null en un alta), que no cuenta como duplicado.
     */
    private void comprobarNombreLibre(String nombre, Integer idPropio) {
        for (Proyecto proyecto : repositorio.findAll()) {
            boolean esOtro = idPropio == null || proyecto.getId() != idPropio.intValue();
            if (esOtro && proyecto.getNombre() != null
                    && proyecto.getNombre().equalsIgnoreCase(nombre)) {
                throw new ConflictoException(
                        "Ya existe un proyecto con el nombre '" + nombre + "': elige otro nombre");
            }
        }
    }
}