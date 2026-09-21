package com.example.gestor.controller;

import com.example.gestor.model.Proyecto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {

    private final List<Proyecto> proyectos = new ArrayList<>();

    @GetMapping
    public List<Proyecto> lista() {
        return proyectos;
    }

    @GetMapping("/{id}")
    public Proyecto detalle(@PathVariable(name = "id") int id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId() == id) {
                return proyecto;
            }
        }

        return null;
    }

    @PostMapping
    public Proyecto crear(@RequestBody Proyecto proyecto) {
        proyectos.add(proyecto);
        return proyecto;
    }
}