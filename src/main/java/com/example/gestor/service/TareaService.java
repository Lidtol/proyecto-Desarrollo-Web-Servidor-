package com.example.gestor.service;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.mapper.TareaMapper;
import com.example.gestor.model.Tarea;
import com.example.gestor.repository.TareaRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TareaService {

    private final TareaRepository repositorio;

    public TareaService(TareaRepository repositorio) {
        this.repositorio = repositorio;
    }

    public List<Tarea> listar(Boolean completada) {
        List<Tarea> seleccion = new ArrayList<>();
        for (Tarea tarea : repositorio.findAll()) {
            if (completada == null || tarea.isCompletada() == completada) {
                seleccion.add(tarea);
            }
        }
        return seleccion;
    }

    public Tarea buscar(int id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("tarea", id));
    }

    public Tarea crear(Tarea tarea) {
        return repositorio.save(tarea);
    }

    public Tarea sustituir(int id, Tarea nueva) {
        buscar(id);
        nueva.setId(id);
        return repositorio.save(nueva);
    }

    public Tarea modificar(int id, TareaPatchRequest cambios) {
        Tarea tarea = buscar(id);
        TareaMapper.aplicarCambios(tarea, cambios);
        return repositorio.save(tarea);
    }

    public void eliminar(int id) {
        repositorio.deleteById(id);
    }
}
