package com.example.gestor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @GetMapping
    public String lista(
            @RequestParam(name = "rol", defaultValue = "todos") String rol) {
        return "Lista de usuarios con rol " + rol;
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable(name = "id") int id) {
        return "Ficha del usuario " + id;
    }
}