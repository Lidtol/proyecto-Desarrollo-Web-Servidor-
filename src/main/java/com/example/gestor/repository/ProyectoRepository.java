package com.example.gestor.repository;

import com.example.gestor.model.MemoriaProyecto;
import com.example.gestor.model.Proyecto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProyectoRepository {

    private final List<Proyecto> proyectos;
    private int siguienteId = 1;

    public ProyectoRepository(MemoriaProyecto memoria) {
        this.proyectos = memoria.getProyectos();
    }

    public List<Proyecto> findAll() {
        return new ArrayList<>(proyectos);
    }

    public Optional<Proyecto> findById(int id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId() == id) {
                return Optional.of(proyecto);
            }
        }
        return Optional.empty();
    }

    public Proyecto save(Proyecto proyecto) {
        if (proyecto.getId() == null) {
            proyecto.setId(siguienteId);
            siguienteId = siguienteId + 1;
            proyectos.add(proyecto);
        }
        return proyecto;
    }

    public boolean deleteById(int id) {
        return proyectos.removeIf(proyecto -> proyecto.getId() == id);
    }

    public boolean existsById(int id) {
        return findById(id).isPresent();
    }
}