package com.example.gestor.controller;

import com.example.gestor.model.Proyecto;
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
    public Proyecto detalle(@PathVariable(name = "id") Integer id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId().equals(id)) {
                return proyecto;
            }
        }
        return null;
    }

    @PostMapping
    public Proyecto crear(@RequestBody Proyecto proyecto) {
        proyecto.setId(siguienteId);
        siguienteId = siguienteId + 1;
        proyectos.add(proyecto);
        return proyecto;
    }

    @PutMapping("/{id}")
    public Proyecto actualizar(@PathVariable(name = "id") Integer id, @RequestBody Proyecto datos) {
        for (int i = 0; i < proyectos.size(); i++) {
            Proyecto proyecto = proyectos.get(i);
            if (proyecto.getId().equals(id)) {
                datos.setId(id);
                proyectos.set(i, datos);
                return datos;
            }
        }
        return null;
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable(name = "id") Integer id) {
        proyectos.removeIf(proyecto -> proyecto.getId().equals(id));
    }
}