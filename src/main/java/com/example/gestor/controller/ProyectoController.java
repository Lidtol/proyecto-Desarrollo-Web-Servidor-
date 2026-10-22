package com.example.gestor.controller;

import com.example.gestor.dto.ProyectoPatchRequest;
import com.example.gestor.dto.ProyectoRequest;
import com.example.gestor.dto.ProyectoResponse;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.error.ConflictoException;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.mapper.ProyectoMapper;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.MemoriaProyecto;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {

    private final List<Proyecto> proyectos;
    private final List<Tarea> tareas;
    private int siguienteId = 1;

    public ProyectoController(MemoriaProyecto memoria) {
        this.proyectos = memoria.getProyectos();
        this.tareas = memoria.getTareas();
    }

    @GetMapping
    public List<ProyectoResponse> lista(
            @RequestParam(name = "activo", required = false) Boolean activo) {
        List<Proyecto> seleccion = new ArrayList<>();
        for (Proyecto proyecto : proyectos) {
            if (activo == null || proyecto.isActivo() == activo) {
                seleccion.add(proyecto);
            }
        }
        return ProyectoMapper.aRespuestas(seleccion);
    }

    @GetMapping("/{id}")
    public ProyectoResponse detalle(@PathVariable(name = "id") int id) {
        return ProyectoMapper.aRespuesta(buscar(id));
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest peticion) {
        Proyecto proyecto = ProyectoMapper.aModelo(peticion);
        comprobarNombreLibre(proyecto.getNombre(), null);

        proyecto.setId(siguienteId);
        siguienteId = siguienteId + 1;
        proyectos.add(proyecto);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(proyecto.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(ProyectoMapper.aRespuesta(proyecto));
    }

    @PutMapping("/{id}")
    public ProyectoResponse sustituir(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody ProyectoRequest peticion) {
        Proyecto actual = buscar(id);
        Proyecto nuevo = ProyectoMapper.aModelo(peticion);
        comprobarNombreLibre(nuevo.getNombre(), id);

        nuevo.setId(id);
        proyectos.set(proyectos.indexOf(actual), nuevo);
        return ProyectoMapper.aRespuesta(nuevo);
    }

    @PatchMapping("/{id}")
    public ProyectoResponse modificar(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody ProyectoPatchRequest cambios) {
        Proyecto proyecto = buscar(id);
        if (cambios.nombre() != null) {
            comprobarNombreLibre(cambios.nombre().trim(), id);
        }
        ProyectoMapper.aplicarCambios(proyecto, cambios);
        return ProyectoMapper.aRespuesta(proyecto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable(name = "id") int id) {
        proyectos.removeIf(proyecto -> proyecto.getId() == id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tareas")
    public List<TareaResponse> tareasDelProyecto(@PathVariable(name = "id") int id) {
        buscar(id);

        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (tarea.getProyectoId() == id) {
                seleccion.add(tarea);
            }
        }
        return TareaMapper.aRespuestas(seleccion);
    }

    private Proyecto buscar(int id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId() == id) {
                return proyecto;
            }
        }
        throw new RecursoNoEncontradoException("proyecto", id);
    }

    /** idPropio: el proyecto que se está modificando (null en un alta), que no cuenta como duplicado. */
    private void comprobarNombreLibre(String nombre, Integer idPropio) {
        for (Proyecto proyecto : proyectos) {
            boolean esOtro = idPropio == null || proyecto.getId() != idPropio.intValue();
            if (esOtro && proyecto.getNombre() != null
                    && proyecto.getNombre().equalsIgnoreCase(nombre)) {
                throw new ConflictoException(
                        "Ya existe un proyecto con el nombre '" + nombre + "': elige otro nombre");
            }
        }
    }
}
