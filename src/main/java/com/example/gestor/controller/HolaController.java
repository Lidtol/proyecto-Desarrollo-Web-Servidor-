package com.example.gestor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HolaController {
    @GetMapping("/hola")
    public String hola() {
        return "Meow, meow, meow, esto es algo diferente a un hola mundo. ";
    }

@GetMapping("/estado")
public String estado() {
    return "Servidor en funcionamiento";
}

@GetMapping("/cosas-de-hacer/resumen")
public String resumen() {
    return "Esta aplicación te ayudará a hacer tus días a días.";


}

@GetMapping("/anyo")
public int anyo() {
    return 2026;
}
}

