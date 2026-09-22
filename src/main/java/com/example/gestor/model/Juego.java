package com.example.gestor.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Juego {

    private int id;
    private String titulo;
    private String plataforma;
    private String genero;
    private boolean completado;

    @JsonCreator
    public Juego() {
    }

    public Juego(int id, String titulo, String plataforma, String genero, boolean completado) {
        this.id = id;
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.genero = genero;
        this.completado = completado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }
}