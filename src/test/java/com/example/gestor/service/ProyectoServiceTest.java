package com.example.gestor.service;

import com.example.gestor.error.ReglaDeNegocioException;
import com.example.gestor.model.Proyecto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProyectoServiceTest {

    private static Proyecto proyecto(Integer id, String nombre) {
        return new Proyecto(id, nombre, "", true, 0);
    }

    @Test
    @DisplayName("No se pueden crear dos proyectos con el mismo nombre, sin distinguir mayúsculas")
    void noSePuedenCrearDosProyectosConElMismoNombre() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(1, "Alpha"));
        ProyectoService servicio = new ProyectoService(proyectos, new TareaRepositorioFalso());

        ReglaDeNegocioException error = assertThrows(ReglaDeNegocioException.class,
                () -> servicio.crear(proyecto(null, "alpha")));

        assertEquals("Ya existe un proyecto con el nombre 'alpha': elige otro nombre", error.getMessage());
        assertEquals(1, proyectos.findAll().size());
    }

    @Test
    @DisplayName("Un proyecto puede conservar su propio nombre al sustituirse")
    void unProyectoPuedeConservarSuPropioNombre() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(1, "Alpha"));
        ProyectoService servicio = new ProyectoService(proyectos, new TareaRepositorioFalso());

        Proyecto resultado = servicio.sustituir(1, proyecto(null, "Alpha"));

        assertEquals(1, resultado.getId());
        assertEquals(1, proyectos.findAll().size());
    }
}
