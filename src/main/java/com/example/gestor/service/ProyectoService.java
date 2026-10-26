package com.example.gestor.service;

import com.example.gestor.dto.ProyectoPatchRequest;
import com.example.gestor.error.ReglaDeNegocioException;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.mapper.ProyectoMapper;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;
import com.example.gestor.repository.ProyectoRepository;
import com.example.gestor.repository.TareaRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProyectoService {

    private final ProyectoRepository proyectos;
    private final TareaRepository tareas;

    public ProyectoService(ProyectoRepository proyectos, TareaRepository tareas) {
        this.proyectos = proyectos;
        this.tareas = tareas;
    }

    public List<Proyecto> listar(Boolean activo) {
        List<Proyecto> seleccion = new ArrayList<>();
        for (Proyecto proyecto : proyectos.findAll()) {
            if (activo == null || proyecto.isActivo() == activo) {
                seleccion.add(proyecto);
            }
        }
        return seleccion;
    }

    public Proyecto buscar(int id) {
        return proyectos.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("proyecto", id));
    }

    public Proyecto crear(Proyecto proyecto) {
        comprobarNombreLibre(proyecto.getNombre(), null);
        return proyectos.save(proyecto);
    }

    public Proyecto sustituir(int id, Proyecto nuevo) {
        buscar(id);
        comprobarNombreLibre(nuevo.getNombre(), id);
        nuevo.setId(id);
        return proyectos.save(nuevo);
    }

    public Proyecto modificar(int id, ProyectoPatchRequest cambios) {
        Proyecto proyecto = buscar(id);
        if (cambios.nombre() != null) {
            comprobarNombreLibre(cambios.nombre().trim(), id);
        }
        ProyectoMapper.aplicarCambios(proyecto, cambios);
        return proyectos.save(proyecto);
    }

    public void eliminar(int id) {
        proyectos.deleteById(id);
    }

    public List<Tarea> tareasDelProyecto(int id) {
        buscar(id);
        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : tareas.findAll()) {
            if (tarea.getProyectoId() == id) {
                seleccion.add(tarea);
            }
        }
        return seleccion;
    }

    /** idPropio: el proyecto que se está modificando (null en un alta), que no cuenta como duplicado. */
    private void comprobarNombreLibre(String nombre, Integer idPropio) {
        for (Proyecto proyecto : proyectos.findAll()) {
            boolean esOtro = idPropio == null || proyecto.getId() != idPropio.intValue();
            if (esOtro && proyecto.getNombre() != null
                    && proyecto.getNombre().equalsIgnoreCase(nombre)) {
                throw new ReglaDeNegocioException(
                        "Ya existe un proyecto con el nombre '" + nombre + "': elige otro nombre");
            }
        }
    }
}
