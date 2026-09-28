package com.example.gestor.controller;

import com.example.gestor.model.Proyecto;
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

    private final List<Proyecto> proyectos = new ArrayList<>();
    private Integer siguienteId = 1;

    @GetMapping
    public List<Proyecto> lista(@RequestParam(name = "activo", required = false) Boolean activo) {
        if (activo == null) {
            return proyectos;
        }

        List<Proyecto> resultado = new ArrayList<>();
        for (Proyecto proyecto : proyectos) {
            if (proyecto.isActivo() == activo) {
                resultado.add(proyecto);
            }
        }
        return resultado;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyecto> detalle(@PathVariable(name = "id") Integer id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId().equals(id)) {
                return ResponseEntity.ok(proyecto);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Proyecto> crear(@RequestBody Proyecto proyecto) {
        proyecto.setId(siguienteId);
        siguienteId = siguienteId + 1;
        proyectos.add(proyecto);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(proyecto.getId())
                .toUri();

        return ResponseEntity.created(ubicacion).body(proyecto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyecto> actualizar(@PathVariable(name = "id") Integer id, @RequestBody Proyecto datos) {
        for (int i = 0; i < proyectos.size(); i++) {
            Proyecto proyecto = proyectos.get(i);
            if (proyecto.getId().equals(id)) {
                datos.setId(id);
                proyectos.set(i, datos);
                return ResponseEntity.ok(datos);
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
    public ResponseEntity<Proyecto> modificar(
            @PathVariable(name = "id") Integer id,
            @RequestBody ProyectoActualizacion cambios) {

        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId().equals(id)) {
                if (cambios.getNombre() != null) {
                    proyecto.setNombre(cambios.getNombre());
                }
                if (cambios.getDescripcion() != null) {
                    proyecto.setDescripcion(cambios.getDescripcion());
                }
                if (cambios.getActivo() != null) {
                    proyecto.setActivo(cambios.getActivo());
                }
                if (cambios.getNumeroDeIncidencias() != null) {
                    proyecto.setNumeroDeIncidencias(cambios.getNumeroDeIncidencias());
                }
                return ResponseEntity.ok(proyecto);
            }
        }

        return ResponseEntity.notFound().build();
    }
}