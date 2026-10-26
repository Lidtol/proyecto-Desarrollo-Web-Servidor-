package com.example.gestor.controller;

import com.example.gestor.dto.ProyectoPatchRequest;
import com.example.gestor.dto.ProyectoRequest;
import com.example.gestor.dto.ProyectoResponse;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.mapper.ProyectoMapper;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.Proyecto;
import com.example.gestor.service.ProyectoService;

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
@RequestMapping("/proyectos")
public class ProyectoController {

    private final ProyectoService servicio;

    public ProyectoController(ProyectoService servicio) {
        this.servicio = servicio;
    }

    @GetMapping
    public List<ProyectoResponse> lista(
            @RequestParam(name = "activo", required = false) Boolean activo) {
        return ProyectoMapper.aRespuestas(servicio.listar(activo));
    }

    @GetMapping("/{id}")
    public ProyectoResponse detalle(@PathVariable(name = "id") int id) {
        return ProyectoMapper.aRespuesta(servicio.buscar(id));
    }

    @PostMapping
    public ResponseEntity<ProyectoResponse> crear(@Valid @RequestBody ProyectoRequest peticion) {
        Proyecto proyecto = servicio.crear(ProyectoMapper.aModelo(peticion));

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
        return ProyectoMapper.aRespuesta(servicio.sustituir(id, ProyectoMapper.aModelo(peticion)));
    }

    @PatchMapping("/{id}")
    public ProyectoResponse modificar(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody ProyectoPatchRequest cambios) {
        return ProyectoMapper.aRespuesta(servicio.modificar(id, cambios));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable(name = "id") int id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/tareas")
    public List<TareaResponse> tareasDelProyecto(@PathVariable(name = "id") int id) {
        return TareaMapper.aRespuestas(servicio.tareasDelProyecto(id));
    }
}
