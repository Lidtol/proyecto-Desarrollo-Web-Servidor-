package com.example.gestor.controller;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.dto.TareaRequest;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.Tarea;
import com.example.gestor.service.TareaService;

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
import java.util.List;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final TareaService servicio;

    public TareaController(TareaService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<TareaResponse> lista(
            @RequestParam(name = "completada", required = false) Boolean completada) {
        return TareaMapper.aRespuestas(servicio.listar(completada));
    }

    @GetMapping("/{id}")
    public TareaResponse detalle(@PathVariable(name = "id") int id) {
        return TareaMapper.aRespuesta(servicio.buscar(id));
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest peticion) {
        Tarea tarea = servicio.crear(TareaMapper.aModelo(peticion));

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
        return TareaMapper.aRespuesta(servicio.sustituir(id, TareaMapper.aModelo(peticion)));
    }

    @PatchMapping("/{id}")
    public TareaResponse modificar(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody TareaPatchRequest cambios) {
        return TareaMapper.aRespuesta(servicio.modificar(id, cambios));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable(name = "id") int id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
