package com.example.gestor.repository;

import com.example.gestor.model.MemoriaProyecto;
import com.example.gestor.model.Tarea;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TareaRepository {

    // Misma lista que ve ProyectoController a través de MemoriaProyecto:
    // así hay una sola lista efectiva y GET /proyectos/{id}/tareas sigue funcionando.
    private final List<Tarea> tareas;
    private int siguienteId = 1;

    public TareaRepository(MemoriaProyecto memoria) {
        this.tareas = memoria.getTareas();
    }

    public List<Tarea> findAll() {
        return new ArrayList<>(tareas);
    }

    public Optional<Tarea> findById(int id) {
        for (Tarea tarea : tareas) {
            if (tarea.getId() == id) {
                return Optional.of(tarea);
            }
        }
        return Optional.empty();
    }

    public Tarea save(Tarea tarea) {
        // Tarea.id es Integer: una tarea nueva tiene id null (no 0).
        if (tarea.getId() == null) {
            tarea.setId(siguienteId);
            siguienteId = siguienteId + 1;
            tareas.add(tarea);
        }
        return tarea;
    }

    public boolean deleteById(int id) {
        return tareas.removeIf(tarea -> tarea.getId() == id);
    }

    public boolean existsById(int id) {
        return findById(id).isPresent();
    }
}