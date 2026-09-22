package com.example.gestor.controller;

import com.example.gestor.model.Juego;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/juegos")
public class JuegosController {

	private final List<Juego> juegos = new ArrayList<>();

	@GetMapping
	public List<Juego> lista() {
		return juegos;
	}

	@GetMapping("/{id}")
	public Juego detalle(@PathVariable(name = "id") int id) {
		for (Juego juego : juegos) {
			if (juego.getId() == id) {
				return juego;
			}
		}

		return null;
	}

	@PostMapping
	public Juego crear(@RequestBody Juego juego) {
		juegos.add(juego);
		return juego;
	}
}
