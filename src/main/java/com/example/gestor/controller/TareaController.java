package com.example.gestor.controller;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.dto.TareaRequest;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.MemoriaProyecto;
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
    private int siguienteId = 1;

    public TareaController(MemoriaProyecto memoria) {
        this.tareas = memoria.getTareas();
    }

    @GetMapping
    public List<TareaResponse> lista(
            @RequestParam(name = "completada", required = false) Boolean completada) {
        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (completada == null || tarea.isCompletada() == completada) {
                seleccion.add(tarea);
            }
        }
        return TareaMapper.aRespuestas(seleccion);
    }

    @GetMapping("/{id}")
    public TareaResponse detalle(@PathVariable(name = "id") int id) {
        return TareaMapper.aRespuesta(buscar(id));
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest peticion) {
        Tarea tarea = TareaMapper.aModelo(peticion);

        tarea.setId(siguienteId);
        siguienteId = siguienteId + 1;
        tareas.add(tarea);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(tarea.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(TareaMapper.aRespuesta(tarea));
    }

    @PutMapping("/{id}")
    public TareaResponse sustituir(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody TareaRequest peticion) {
        Tarea actual = buscar(id);
        Tarea nueva = TareaMapper.aModelo(peticion);

        nueva.setId(id);
        tareas.set(tareas.indexOf(actual), nueva);
        return TareaMapper.aRespuesta(nueva);
    }

    @PatchMapping("/{id}")
    public TareaResponse modificar(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody TareaPatchRequest cambios) {
        Tarea tarea = buscar(id);
        TareaMapper.aplicarCambios(tarea, cambios);
        return TareaMapper.aRespuesta(tarea);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable(name = "id") int id) {
        tareas.removeIf(tarea -> tarea.getId() == id);
        return ResponseEntity.noContent().build();
    }

    private Tarea buscar(int id) {
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return tarea;
            }
        }
        throw new RecursoNoEncontradoException("tarea", id);
    }
}
