package com.example.gestor.repository;

import com.example.gestor.model.Proyecto;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProyectoRepositorioEnMemoria implements ProyectoRepository {

    private final List<Proyecto> proyectos = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public List<Proyecto> findAll() {
        return new ArrayList<>(proyectos);
    }

    @Override
    public Optional<Proyecto> findById(int id) {
        for (Proyecto proyecto : proyectos) {
            if (proyecto.getId() == id) {
                return Optional.of(proyecto);
            }
        }
        return Optional.empty();
    }

    @Override
    public Proyecto save(Proyecto proyecto) {
        if (proyecto.getId() == null) {
            proyecto.setId(siguienteId);
            siguienteId = siguienteId + 1;
            proyectos.add(proyecto);
            return proyecto;
        }
        for (int i = 0; i < proyectos.size(); i++) {
            if (proyectos.get(i).getId().equals(proyecto.getId())) {
                proyectos.set(i, proyecto);
                return proyecto;
            }
        }
        proyectos.add(proyecto);
        return proyecto;
    }

    @Override
    public boolean deleteById(int id) {
        return proyectos.removeIf(proyecto -> proyecto.getId() == id);
    }

    @Override
    public boolean existsById(int id) {
        return findById(id).isPresent();
    }
}
