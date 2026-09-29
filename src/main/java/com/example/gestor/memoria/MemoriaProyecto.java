package com.example.gestor.memoria;

import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;

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