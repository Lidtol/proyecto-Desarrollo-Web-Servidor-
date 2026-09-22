package com.example.gestor.controller;

import com.example.gestor.model.Tarea;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    private final List<Tarea> tareas = new ArrayList<>();
    private Integer siguienteId = 1;

    @GetMapping
    public List<Tarea> lista(
            @RequestParam(name = "completada", required = false) Boolean completada) {
        if (completada == null) {
            return tareas;
        }
        List<Tarea> resultado = new ArrayList<>();
        for (Tarea tarea : tareas) {
            if (tarea.isCompletada() == completada) {
                resultado.add(tarea);
            }
        }
        return resultado;
    }

    @GetMapping("/{id}")
    public Tarea detalle(@PathVariable(name = "id") Integer id) {
        for (Tarea tarea : tareas) {
            if (tarea.getId().equals(id)) {
                return tarea;
            }
        }
        return null;
    }

    @PostMapping
    public Tarea crear(@RequestBody Tarea tarea) {
        tarea.setId(siguienteId);
        siguienteId = siguienteId + 1;
        tareas.add(tarea);
        return tarea;
    }

    @PutMapping("/{id}")
    public Tarea actualizar(
            @PathVariable(name = "id") Integer id,
            @RequestBody Tarea datos) {

        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getId().equals(id)) {
                datos.setId(id);
                tareas.set(i, datos);
                return datos;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable(name = "id") Integer id) {
        tareas.removeIf(tarea -> tarea.getId().equals(id));
    }
}