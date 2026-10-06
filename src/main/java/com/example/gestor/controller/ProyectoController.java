package com.example.gestor.controller;

import com.example.gestor.dto.ProyectoPatchRequest;
import com.example.gestor.dto.ProyectoRequest;
import com.example.gestor.dto.ProyectoResponse;
import com.example.gestor.memoria.MemoriaProyecto;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    public ProyectoController(MemoriaProyecto memoria) {
        this.proyectos = memoria.getProyectos();
        this.tareas = memoria.getTareas();
    }

    private Integer siguienteId = 1;

    @GetMapping("/{id}/tareas")
    public ResponseEntity<List<Tarea>> tareasDelProyecto(
            @PathVariable(name = "id") int id) {

        boolean existe = false;

        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId() == id) {
                existe = true;
                break;
            }
        }

        if (!existe) {
            return ResponseEntity.notFound().build();
        }

        List<Tarea> resultado = new ArrayList<>();

        for (Tarea tarea : tareas) {
            if (tarea.getProyectoId() == id) {
                resultado.add(tarea);
            }
        }

        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponse> detalle(@PathVariable(name = "id") Integer id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId().equals(id)) {
                return ResponseEntity.ok(ProyectoResponse.desde(proyecto));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public List<ProyectoResponse> lista(
            @RequestParam(name = "activo", required = false) Boolean activo) {
        if (activo == null) {
            List<ProyectoResponse> resultado = new ArrayList<>();
            for (Proyecto proyecto : proyectos) {
                resultado.add(ProyectoResponse.desde(proyecto));
            }
            return resultado;
        }
        List<ProyectoResponse> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos) {
            if (proyecto.isActivo() == activo) {
                resultado.add(ProyectoResponse.desde(proyecto));
            }
        }
        return resultado;
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest peticion) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(peticion.nombre());
        proyecto.setDescripcion(peticion.descripcion());
        proyecto.setActivo(peticion.activo());
        proyecto.setNumeroDeIncidencias(peticion.numeroDeIncidencias());

        proyecto.setId(siguienteId);
        siguienteId = siguienteId + 1;
        proyectos.add(proyecto);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(proyecto.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(ProyectoResponse.desde(proyecto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProyectoResponse> actualizar(@PathVariable(name = "id") Integer id, @Valid @RequestBody ProyectoRequest peticion) {
        Proyecto datos = new Proyecto();
        datos.setNombre(peticion.nombre());
        datos.setDescripcion(peticion.descripcion());
        datos.setActivo(peticion.activo());
        datos.setNumeroDeIncidencias(peticion.numeroDeIncidencias());

        for (int i = 0; i < proyectos.size(); i++) {
            Proyecto proyecto = proyectos.get(i);
            if (proyecto.getId().equals(id)) {
                datos.setId(id);
                proyectos.set(i, datos);
                return ResponseEntity.ok(ProyectoResponse.desde(datos));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable(name = "id") Integer id) {
        proyectos.removeIf(proyecto -> proyecto.getId().equals(id));
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProyectoResponse> modificar(
            @PathVariable(name = "id") Integer id,
            @Valid @RequestBody ProyectoPatchRequest cambios) {

        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId().equals(id)) {
                if (cambios.nombre() != null) {
                    proyecto.setNombre(cambios.nombre());
                }
                if (cambios.descripcion() != null) {
                    proyecto.setDescripcion(cambios.descripcion());
                }
                if (cambios.activo() != null) {
                    proyecto.setActivo(cambios.activo());
                }
                if (cambios.numeroDeIncidencias() != null) {
                    proyecto.setNumeroDeIncidencias(cambios.numeroDeIncidencias());
                }
                return ResponseEntity.ok(ProyectoResponse.desde(proyecto));
            }
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/tareas")
    public ResponseEntity<Tarea> crearTareaEnProyecto(
            @PathVariable(name = "id") Integer id,
            @RequestBody Tarea tarea) {

        // comprobar que el proyecto existe
        boolean existe = false;
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId().equals(id)) {
                existe = true;
                break;
            }
        }

        if (!existe) {
            return ResponseEntity.notFound().build();
        }

        // asignar proyectoId y calcular id para la tarea
        tarea.setProyectoId(id);
        int siguienteTareaId = 1;
        for (Tarea t : tareas) {
            if (t.getId() != null && t.getId() >= siguienteTareaId) {
                siguienteTareaId = t.getId() + 1;
            }
        }
        tarea.setId(siguienteTareaId);
        tareas.add(tarea);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{tareaId}")
                .buildAndExpand(tarea.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(tarea);
    }

    @GetMapping("/{id}/tareas/{tareaId}")
    public ResponseEntity<Tarea> detalleTareaEnProyecto(
            @PathVariable(name = "id") Integer id,
            @PathVariable(name = "tareaId") Integer tareaId) {

        for (Tarea tarea : tareas) {
            if (tarea.getId() != null && tarea.getId().equals(tareaId) && tarea.getProyectoId() == id) {
                return ResponseEntity.ok(tarea);
            }
        }

        return ResponseEntity.notFound().build();
    }

}