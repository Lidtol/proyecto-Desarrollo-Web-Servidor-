package com.example.gestor.error;

public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super("No existe " + recurso + " con id " + id);
    }
}