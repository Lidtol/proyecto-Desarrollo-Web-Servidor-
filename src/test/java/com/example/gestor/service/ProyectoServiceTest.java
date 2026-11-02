package com.example.gestor.service;

import com.example.gestor.error.ReglaDeNegocioException;
import com.example.gestor.error.RecursoNoEncontradoException;
import com.example.gestor.model.ProyectoCambios;
import com.example.gestor.model.Proyecto;
import com.example.gestor.model.Tarea;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    @DisplayName("PATCH no permite poner el nombre de otro proyecto")
    void patchNoPermiteDuplicarNombreDeOtroProyecto() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(1, "Alpha"), proyecto(2, "Beta"));
        ProyectoService servicio = new ProyectoService(proyectos, new TareaRepositorioFalso());

        ReglaDeNegocioException error = assertThrows(ReglaDeNegocioException.class,
                () -> servicio.modificar(1, new ProyectoCambios("beta", null, null, null)));

        assertEquals("Ya existe un proyecto con el nombre 'beta': elige otro nombre", error.getMessage());
        assertEquals("Alpha", servicio.buscar(1).getNombre());
    }

    @Test
    @DisplayName("Listar por activo devuelve solo los proyectos del estado pedido")
    void listarFiltraPorActivo() {
        Proyecto activo = new Proyecto(1, "Activo", "", true, 0);
        Proyecto archivado = new Proyecto(2, "Archivado", "", false, 0);
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(activo, archivado);
        ProyectoService servicio = new ProyectoService(proyectos, new TareaRepositorioFalso());

        List<Proyecto> resultado = servicio.listar(true);

        assertEquals(1, resultado.size());
        assertEquals("Activo", resultado.get(0).getNombre());
    }

    @Test
    @DisplayName("La ruta anidada de tareas falla si el proyecto no existe")
    void tareasDelProyectoFallaSiNoExisteProyecto() {
        ProyectoService servicio = new ProyectoService(new ProyectoRepositorioFalso(), new TareaRepositorioFalso());

        assertThrows(RecursoNoEncontradoException.class, () -> servicio.tareasDelProyecto(99));
    }

    @Test
    @DisplayName("La ruta anidada de tareas devuelve solo las tareas del proyecto pedido")
    void tareasDelProyectoDevuelveSoloLasDelProyecto() {
        ProyectoRepositorioFalso proyectos = new ProyectoRepositorioFalso();
        proyectos.con(proyecto(1, "Alpha"), proyecto(2, "Beta"));

        TareaRepositorioFalso tareas = new TareaRepositorioFalso();
        Tarea t1 = new Tarea();
        t1.setTitulo("A");
        t1.setPrioridad("alta");
        t1.setProyectoId(1);
        tareas.save(t1);

        Tarea t2 = new Tarea();
        t2.setTitulo("B");
        t2.setPrioridad("media");
        t2.setProyectoId(2);
        tareas.save(t2);

        Tarea t3 = new Tarea();
        t3.setTitulo("C");
        t3.setPrioridad("baja");
        t3.setProyectoId(1);
        tareas.save(t3);

        ProyectoService servicio = new ProyectoService(proyectos, tareas);

        List<Tarea> resultado = servicio.tareasDelProyecto(1);

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(t -> t.getProyectoId() == 1));
    }
}
