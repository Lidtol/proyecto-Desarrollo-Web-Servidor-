package com.example.gestor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController 

@RequestMapping ("/proyectos")

public class ProyectoController {

    @GetMapping
    public String estado(
            @RequestParam(name = "estados", defaultValue = "todos") String estados) {
        return "Lista de proyectos" + estados;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable(name = "id") int id) {
        return "Ficha del proyecto " + id;
    }

    @GetMapping("{id}/incidencias")
    public String incidencias(@PathVariable(name = "id") int id) {
        return "incidencias del proyecto " + id;
    }


    @GetMapping("{proyecto}/incidencias/{incidenciaId}")
    public String incidenciasProyecto(
        @PathVariable(name = "incidenciaId") int incidenciaId,
        @PathVariable (name = "proyecto") String proyecto)
        
    // Datos en la ruta: "proyecto" e "incidenciaId".
    // No hay datos en la query string.
    // Prueba de la regla: los datos que forman parte de la URL
    // y están definidos entre {} en @GetMapping son variables de ruta.
        
        {
        return "incidencias del proyecto " + proyecto + " numero " + incidenciaId;
    }



    @GetMapping("/pagina/{prioridad}")
    public String prioridadPagina(
        @PathVariable(name = "prioridad") int prioridad)
        
    // Dato en la ruta: "prioridad".
    // No hay datos en la query string.
    // Prueba de la regla: "prioridad" aparece entre {} en @GetMapping,
    // por lo que es una variable de ruta y se obtiene mediante @PathVariable.
        
        {
        return "La prioridad de la página es "  + prioridad;
    }

}
