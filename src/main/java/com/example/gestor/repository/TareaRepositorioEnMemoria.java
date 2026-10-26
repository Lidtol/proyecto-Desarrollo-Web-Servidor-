package com.example.gestor.repository;

import com.example.gestor.model.Tarea;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TareaRepositorioEnMemoria implements TareaRepository {

    private final List<Tarea> tareas = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public List<Tarea> findAll() {
        return new ArrayList<>(tareas);
    }

    @Override
    public Optional<Tarea> findById(int id) {
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return Optional.of(tarea);
            }
        }
        return Optional.empty();
    }

    @Override
    public Tarea save(Tarea tarea) {
        if (tarea.getId() == null) {
            tarea.setId(siguienteId);
            siguienteId = siguienteId + 1;
            tareas.add(tarea);
            return tarea;
        }
        for (int i = 0; i < tareas.size(); i++) {
            if (tareas.get(i).getId().equals(tarea.getId())) {
                tareas.set(i, tarea);
                return tarea;
            }
        }
        tareas.add(tarea);
        return tarea;
    }

    @Override
    public boolean deleteById(int id) {
        return tareas.removeIf(tarea -> tarea.getId() == id);
    }

    @Override
    public boolean existsById(int id) {
        return findById(id).isPresent();
    }
}
