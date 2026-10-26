package com.example.gestor.service;

import com.example.gestor.model.Proyecto;
import com.example.gestor.repository.ProyectoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Doble de prueba: misma lógica que la implementación en memoria, sin Spring. */
class ProyectoRepositorioFalso implements ProyectoRepository {

    private final List<Proyecto> proyectos = new ArrayList<>();

    /** Prepara el escenario: estos proyectos existen (con el id que traen) y ninguno más. */
    void con(Proyecto... iniciales) {
        proyectos.addAll(List.of(iniciales));
    }

    @Override
    public List<Proyecto> findAll() {
        return new ArrayList<>(proyectos);
    }

    @Override
    public Optional<Proyecto> findById(int id) {
        return proyectos.stream().filter(p -> p.getId() == id).findFirst();
    }

    @Override
    public Proyecto save(Proyecto proyecto) {
        if (proyecto.getId() == null) {
            int siguiente = proyectos.stream().mapToInt(Proyecto::getId).max().orElse(0) + 1;
            proyecto.setId(siguiente);
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
        return proyectos.removeIf(p -> p.getId() == id);
    }

    @Override
    public boolean existsById(int id) {
        return findById(id).isPresent();
    }
}
