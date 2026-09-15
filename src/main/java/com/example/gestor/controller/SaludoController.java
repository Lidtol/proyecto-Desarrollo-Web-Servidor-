package com.example.gestor.controller;

import java.time.LocalDate;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SaludoController {

@GetMapping("/saludo")
public String saludo(
        @RequestParam(name = "nombre", defaultValue = "Meowmeow") String nombre) {
            
    return "Hola, " + nombre + ".";
}


//@GetMapping("/proyectos/{proyectoId}/incidencias/{incidenciaId}")
//public String incidenciaDeProyecto(
//        @PathVariable(name = "proyectoId") int proyectoId,
 //       @PathVariable(name = "incidenciaId") int incidenciaId) {
//
  //  return "Incidencia " + incidenciaId + " del proyecto " + proyectoId;
//}


@GetMapping("/informes")
public String informes(
        @RequestParam(name = "desde") LocalDate desde,
        @RequestParam(name = "activo", defaultValue = "true") boolean activo) {

    return "Desde " + desde + " (día " + desde.getDayOfMonth()
            + " del mes " + desde.getMonthValue() + "), activo=" + activo;
}

}