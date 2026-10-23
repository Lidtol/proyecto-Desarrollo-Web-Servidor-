package com.example.gestor.model;


import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MemoriaProyecto {

    private final List<Proyecto> proyectos = new ArrayList<>();
    private final List<Tarea> tareas = new ArrayList<>();

    public List<Proyecto> getProyectos() {
        return proyectos;
    }

    public List<Tarea> getTareas() {
        return tareas;
    }
}