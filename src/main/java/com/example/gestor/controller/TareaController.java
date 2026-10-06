package com.example.gestor.controller;

import com.example.gestor.dto.TareaRequest;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.memoria.MemoriaProyecto;
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
@RequestMapping("/tareas")

public class TareaController {

    private final List<Tarea> tareas;

    public TareaController(MemoriaProyecto memoria) {
        this.tareas = memoria.getTareas();
    }

    private Integer siguienteId = 1;

    @GetMapping
    public List<TareaResponse> lista(
            @RequestParam(name = "completada", required = false) Boolean completada) {
        if (completada == null) {
            List<TareaResponse> resultado = new ArrayList<>();
            for (Tarea tarea : tareas) {
                resultado.add(TareaResponse.desde(tarea));
            }
            return resultado;
        }
        List<TareaResponse> resultado = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (tarea.isCompletada() == completada) {
                resultado.add(TareaResponse.desde(tarea));
            }
        }
        return resultado;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TareaResponse> detalle(@PathVariable(name = "id") int id) {
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return ResponseEntity.ok(TareaResponse.desde(tarea));
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(
            @PathVariable(name = "id") int id) {

        tareas.removeIf(tarea -> tarea.getId() == id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(
            @Valid @RequestBody TareaRequest request) {

        Tarea tarea = new Tarea();
        tarea.setTitulo(request.titulo());
        tarea.setPrioridad(request.prioridad());
        tarea.setProyectoId(request.proyectoId());
        tarea.setCompletada(Boolean.TRUE.equals(request.completada()));

        tarea.setId(siguienteId);
        siguienteId = siguienteId + 1;
        tareas.add(tarea);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarea.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(TareaResponse.desde(tarea));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TareaResponse> actualizar(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody TareaRequest request) {

        Tarea datos = new Tarea();
        datos.setTitulo(request.titulo());
        datos.setPrioridad(request.prioridad());
        datos.setProyectoId(request.proyectoId());
        datos.setCompletada(Boolean.TRUE.equals(request.completada()));

        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getId() == id) {

                datos.setId(id);

                tareas.set(i, datos);

                return ResponseEntity.ok(TareaResponse.desde(datos));
            }
        }

        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TareaResponse> modificar(
            @PathVariable(name = "id") int id,
            @RequestBody TareaActualizacion cambios) {

        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {

                if (cambios.getTitulo() != null) {
                    tarea.setTitulo(cambios.getTitulo());
                }

                if (cambios.getPrioridad() != null) {
                    tarea.setPrioridad(cambios.getPrioridad());
                }

                if (cambios.getCompletada() != null) {
                    tarea.setCompletada(cambios.getCompletada());
                }

                return ResponseEntity.ok(TareaResponse.desde(tarea));
            }
        }

        return ResponseEntity.notFound().build();
    }

    public static class TareaActualizacion {

        private String titulo;
        private String prioridad;
        private Boolean completada;

        public TareaActualizacion() {
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getPrioridad() {
            return prioridad;
        }

        public void setPrioridad(String prioridad) {
            this.prioridad = prioridad;
        }

        public Boolean getCompletada() {
            return completada;
        }

        public void setCompletada(Boolean completada) {
            this.completada = completada;
        }
    }
}