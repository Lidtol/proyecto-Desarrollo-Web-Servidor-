package com.example.gestor.controller;

import com.example.gestor.model.Videojuego;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/videojuegos")
public class VideojuegoController {

    private final List<Videojuego> videojuegos = new ArrayList<>();

    @GetMapping
    public List<Videojuego> lista() {
        return videojuegos;
    }

    @GetMapping("/{id}")
    public Videojuego detalle(@PathVariable(name = "id") int id) {
        for (Videojuego videojuego : videojuegos) {
            if (videojuego.getId() == id) {
                return videojuego;
            }
        }

        return null;
    }

    @PostMapping
    public Videojuego crear(@RequestBody Videojuego videojuego) {
        videojuegos.add(videojuego);
        return videojuego;
    }
}