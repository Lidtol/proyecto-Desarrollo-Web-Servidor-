package com.example.gestor.controller;

import com.example.gestor.model.Gato;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/gatos")
public class GatosController {

    private final List<Gato> gatos = new ArrayList<>();

    @GetMapping
    public List<Gato> lista() {
        return gatos;
    }

    @GetMapping("/{id}")
    public Gato detalle(@PathVariable(name = "id") int id) {
        for (Gato gato : gatos) {
            if (gato.getId() == id) {
                return gato;
            }
        }

        return null;
    }

    @PostMapping
    public Gato crear(@RequestBody Gato gato) {
        gatos.add(gato);
        return gato;
    }
}