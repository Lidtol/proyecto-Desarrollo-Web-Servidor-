package com.example.gestor.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Gato {

    private int id;
    private String nombre;
    private String raza;
    private int edad;
    private boolean adoptado;

    @JsonCreator
    public Gato() {
    }

    public Gato(int id, String nombre, String raza, int edad, boolean adoptado) {
        this.id = id;
        this.nombre = nombre;
        this.raza = raza;
        this.edad = edad;
        this.adoptado = adoptado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isAdoptado() {
        return adoptado;
    }

    public void setAdoptado(boolean adoptado) {
        this.adoptado = adoptado;
    }
}