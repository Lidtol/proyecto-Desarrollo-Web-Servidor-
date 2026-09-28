package com.example.gestor.controller;

public class ProyectoActualizacion {

    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer numeroDeIncidencias;

    public ProyectoActualizacion() {
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getNumeroDeIncidencias() {
        return numeroDeIncidencias;
    }

    public void setNumeroDeIncidencias(Integer numeroDeIncidencias) {
        this.numeroDeIncidencias = numeroDeIncidencias;
    }
}