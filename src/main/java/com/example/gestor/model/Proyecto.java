package com.example.gestor.model;

public class Proyecto {

    private Integer id;
    private String nombre;
    private String descripcion;
    private boolean activo;
    private int numeroDeIncidencias;
    private java.time.LocalDate fechaInicio;
    private java.time.LocalDate fechaFin;

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

    public java.time.LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(java.time.LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public java.time.LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(java.time.LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public int getNumeroDeIncidencias() {
        return numeroDeIncidencias;
    }

    public void setNumeroDeIncidencias(int numeroDeIncidencias) {
        this.numeroDeIncidencias = numeroDeIncidencias;
    }
}