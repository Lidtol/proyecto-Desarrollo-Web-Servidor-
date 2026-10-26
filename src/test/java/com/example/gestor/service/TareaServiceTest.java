package com.example.gestor.service;

import com.example.gestor.dto.TareaPatchRequest;
import com.example.gestor.error.ReglaDeNegocioException;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TareaServiceTest {

    private static Proyecto proyecto(int id, String nombre, boolean activo) {
        return new Proyecto(id, nombre, "", activo, 0);
    }

    private static Tarea tarea(int proyectoId) {
        Tarea tarea = new Tarea();
        tarea.setTitulo("Revisar el login");
        tarea.setPrioridad("alta");
        tarea.setProyectoId(proyectoId);
        return tarea;
    }

    @Test
    @DisplayName("Una tarea nueva nace sin completar")
    void unaTareaNuevaNaceSinCompletar() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(7, "Web corporativa", true));
        TareaService servicio = new TareaService(new TareaRepositorioFalso(), proyectos);

        Tarea entrada = tarea(7);
        entrada.setCompletada(true); // el cliente insiste

        Tarea creada = servicio.crear(entrada);

        assertFalse(creada.isCompletada());
    }

    @Test
    @DisplayName("Una tarea en un proyecto activo queda guardada")
    void unaTareaEnUnProyectoActivoQuedaGuardada() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(7, "Web corporativa", true));
        TareaRepositorioFalso tareas = new TareaRepositorioFalso();
        TareaService servicio = new TareaService(tareas, proyectos);

        Tarea creada = servicio.crear(tarea(7));

        assertEquals(1, creada.getId());
        assertEquals(1, tareas.findAll().size());
    }

    @Test
    @DisplayName("No se puede crear una tarea en un proyecto que no existe")
    void noSePuedeCrearEnUnProyectoInexistente() {
        TareaRepositorioFalso tareas = new TareaRepositorioFalso();
        TareaService servicio = new TareaService(tareas, new ProyectoRepositorioFalso());

        ReglaDeNegocioException error = assertThrows(
                ReglaDeNegocioException.class, () -> servicio.crear(tarea(999)));

        assertEquals("No existe el proyecto 999", error.getMessage());
        assertTrue(tareas.findAll().isEmpty(), "el rechazo no debe insertar nada");
    }

    @Test
    @DisplayName("No se puede crear una tarea en un proyecto inactivo")
    void noSePuedeCrearEnUnProyectoInactivo() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(3, "Archivado", false));
        TareaRepositorioFalso tareas = new TareaRepositorioFalso();
        TareaService servicio = new TareaService(tareas, proyectos);

        ReglaDeNegocioException error = assertThrows(
                ReglaDeNegocioException.class, () -> servicio.crear(tarea(3)));

        assertEquals("El proyecto 3 está archivado y no admite tareas", error.getMessage());
        assertTrue(tareas.findAll().isEmpty(), "el rechazo no debe insertar nada");
    }

    @Test
    @DisplayName("Sustituir una tarea moviéndola a un proyecto inactivo se rechaza y conserva el estado previo")
    void sustituirHaciaUnProyectoInactivoSeRechaza() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(1, "Activo", true), proyecto(2, "Archivado", false));
        TareaService servicio = new TareaService(new TareaRepositorioFalso(), proyectos);
        Tarea creada = servicio.crear(tarea(1));

        assertThrows(ReglaDeNegocioException.class,
                () -> servicio.sustituir(creada.getId(), tarea(2)));

        assertEquals(1, servicio.buscar(creada.getId()).getProyectoId());
    }

    @Test
    @DisplayName("Mover una tarea con PATCH a un proyecto inexistente se rechaza")
    void moverConPatchAUnProyectoInexistenteSeRechaza() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(1, "Activo", true));
        TareaService servicio = new TareaService(new TareaRepositorioFalso(), proyectos);
        Tarea creada = servicio.crear(tarea(1));
        TareaPatchRequest cambios = new TareaPatchRequest(null, null, 50, null);

        ReglaDeNegocioException error = assertThrows(ReglaDeNegocioException.class,
                () -> servicio.modificar(creada.getId(), cambios));

        assertEquals("No existe el proyecto 50", error.getMessage());
        assertEquals(1, servicio.buscar(creada.getId()).getProyectoId());
    }

    @Test
    @DisplayName("Una tarea de un proyecto archivado después sigue pudiendo completarse")
    void unaTareaDeUnProyectoArchivadoPuedeCompletarse() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        Proyecto proyecto = proyecto(1, "Se archivará", true);
        proyectos.con(proyecto);
        TareaService servicio = new TareaService(new TareaRepositorioFalso(), proyectos);
        Tarea creada = servicio.crear(tarea(1));
        proyecto.setActivo(false); // el proyecto se archiva después

        Tarea modificada = servicio.modificar(creada.getId(),
                new TareaPatchRequest(null, null, null, true));

        assertTrue(modificada.isCompletada());
    }
}
