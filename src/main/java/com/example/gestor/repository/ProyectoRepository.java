package com.example.gestor.repository;

import com.example.gestor.model.Proyecto;

import java.util.List;
import java.util.Optional;

public interface ProyectoRepository {

    List<Proyecto> findAll();

    Optional<Proyecto> findById(int id);

    /** Si el proyecto no tiene id lo asigna (alta); si ya lo tiene, sustituye el existente. */
    Proyecto save(Proyecto proyecto);

    boolean deleteById(int id);

    boolean existsById(int id);
}
