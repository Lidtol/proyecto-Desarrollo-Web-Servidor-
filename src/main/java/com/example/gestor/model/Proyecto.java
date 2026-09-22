package com.example.gestor.model;

public class Proyecto {

    private Integer id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private int numeroDeIncidencias;

    public Proyecto() {
    }

    public Proyecto(Integer id, String nombre, String descripcion, boolean activo, int numeroDeIncidencias) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.activo = activo;
        this.numeroDeIncidencias = numeroDeIncidencias;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getNumeroDeIncidencias() {
        return numeroDeIncidencias;
    }

    public void setNumeroDeIncidencias(int numeroDeIncidencias) {
        this.numeroDeIncidencias = numeroDeIncidencias;
    }
}