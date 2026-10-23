package com.example.gestor.controller;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.dto.TareaRequest;
import com.example.gestor.dto.TareaResponse;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.MemoriaProyecto;
import com.example.gestor.model.Tarea;
import com.example.gestor.repository.TareaRepository;
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

    // Se mantiene el constructor con MemoriaProyecto: Spring lo sigue inyectando
    // y TareaControllerTest sigue compilando sin tocarlo.
    public TareaController(MemoriaProyecto memoria) {
        this.servicio = new TareaService(new TareaRepository(memoria));
    }

    @GetMapping
    public List<TareaResponse> lista(
            @RequestParam(name = "completada", required = false) Boolean completada) {
        return TareaMapper.aRespuestas(servicio.listar(completada));
    }

    @GetMapping("/{id}")
    public TareaResponse detalle(@PathVariable(name = "id") int id) {
        return TareaMapper.aRespuesta(servicio.obtener(id));
    }

    @PostMapping
    public ResponseEntity<TareaResponse> crear(@Valid @RequestBody TareaRequest peticion) {
        Tarea creada = servicio.crear(TareaMapper.aModelo(peticion));

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creada.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(TareaMapper.aRespuesta(creada));
    }

    @PutMapping("/{id}")
    public TareaResponse sustituir(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody TareaRequest peticion) {
        return TareaMapper.aRespuesta(servicio.reemplazar(id, TareaMapper.aModelo(peticion)));
    }

    @PatchMapping("/{id}")
    public TareaResponse modificar(
            @PathVariable(name = "id") int id,
            @Valid @RequestBody TareaPatchRequest cambios) {
        Tarea modificada = servicio.modificar(
                id, cambios.titulo(), cambios.prioridad(), cambios.proyectoId(), cambios.completada());
        return TareaMapper.aRespuesta(modificada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable(name = "id") int id) {
        servicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}