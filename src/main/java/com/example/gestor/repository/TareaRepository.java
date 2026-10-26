package com.example.gestor.repository;

import com.example.gestor.model.Tarea;

import java.util.List;
import java.util.Optional;

public interface TareaRepository {

    List<Tarea> findAll();

    Optional<Tarea> findById(int id);

    /** Si la tarea no tiene id lo asigna (alta); si ya lo tiene, sustituye la existente. */
    Tarea save(Tarea tarea);

    boolean deleteById(int id);

    boolean existsById(int id);
}
