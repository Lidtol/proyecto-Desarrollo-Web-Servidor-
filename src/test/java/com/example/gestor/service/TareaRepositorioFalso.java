package com.example.gestor.service;

import com.example.gestor.model.Tarea;
import com.example.gestor.repository.TareaRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Doble de prueba: misma lógica que la implementación en memoria, ids controlados (1, 2, 3...). */
class TareaRepositorioFalso implements TareaRepository {

    private final List<Tarea> tareas = new ArrayList<>();
    private int siguienteId = 1;

    @Override
    public List<Tarea> findAll() {
        return new ArrayList<>(tareas);
    }

    @Override
    public Optional<Tarea> findById(int id) {
        return tareas.stream().filter(t -> t.getId() == id).findFirst();
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
        return tareas.removeIf(t -> t.getId() == id);
    }

    @Override
    public boolean existsById(int id) {
        return findById(id).isPresent();
    }
}
