package com.example.gestor.controller;

import com.example.gestor.error.ManejadorDeErrores;
import com.example.gestor.repository.ProyectoRepositorioEnMemoria;
import com.example.gestor.repository.TareaRepositorioEnMemoria;
import com.example.gestor.service.ProyectoService;
import com.example.gestor.service.TareaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Todos los errores deben salir con la misma forma: status, title, detail, instance, momento. */
class ErroresTest {

    private static final String PROYECTO_OK =
            "{\"nombre\":\"Alpha\",\"descripcion\":\"Base\",\"activo\":true,\"numeroDeIncidencias\":1}";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        TareaRepositorioEnMemoria tareas = new TareaRepositorioEnMemoria();
        ProyectoRepositorioEnMemoria proyectos = new ProyectoRepositorioEnMemoria();
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ProyectoController(new ProyectoService(proyectos, tareas)),
                        new TareaController(new TareaService(tareas, proyectos)))
                .setControllerAdvice(new ManejadorDeErrores())
                .build();
    }

    private void crearProyecto() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON).content(PROYECTO_OK))
                .andExpect(status().isCreated());
    }

    // --- Los casos correctos no se alteran ---

    @Test
    void altaValidaSigueDevolviendo201ConLocation() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON).content(PROYECTO_OK))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/proyectos/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").doesNotExist());
    }

    @Test
    void deleteDevuelve204AunqueNoExista() throws Exception {
        mockMvc.perform(delete("/tareas/99")).andExpect(status().isNoContent());
        mockMvc.perform(delete("/proyectos/99")).andExpect(status().isNoContent());
    }

    // --- 400: cuerpo ilegible ---

    @Test
    void cuerpoIlegibleDevuelve400() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON).content("{esto no es json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("El cuerpo de la petición no es un JSON válido o le faltan datos"))
                .andExpect(jsonPath("$.instance").value("/proyectos"));
    }

    // --- 400: campo inválido ---

    @Test
    void nombreDemasiadoCortoIndicaElCampoRechazado() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"AB\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.instance").value("/proyectos"))
                .andExpect(jsonPath("$.invalidParams[0].campo").value("nombre"))
                .andExpect(jsonPath("$.invalidParams[0].motivo").value("El nombre debe tener entre 3 y 80 caracteres"));
    }

    @Test
    void tareaSinPrioridadDevuelve400EnEspanol() throws Exception {
        mockMvc.perform(post("/tareas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Comprar\",\"proyectoId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams[0].campo").value("prioridad"))
                .andExpect(jsonPath("$.invalidParams[0].motivo").value("La prioridad es obligatoria: usa baja, media o alta"));
    }

    // --- 400: regla propia ---

    @Test
    void prioridadFueraDeLaListaLaRechazaLaReglaPropia() throws Exception {
        mockMvc.perform(post("/tareas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Comprar\",\"prioridad\":\"urgente\",\"proyectoId\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams[0].campo").value("prioridad"))
                .andExpect(jsonPath("$.invalidParams[0].motivo").value("La prioridad debe ser una de estas: baja, media o alta"));
    }

    @Test
    void prioridadTambienSeValidaEnElPatch() throws Exception {
        crearProyecto();
        mockMvc.perform(post("/tareas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Comprar\",\"prioridad\":\"alta\",\"proyectoId\":1}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/tareas/1").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"prioridad\":\"urgente\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams[0].campo").value("prioridad"));
    }

    @Test
    void fechaFinAnteriorAFechaInicioLaRechazaLaReglaDeClase() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Alpha\",\"fechaInicio\":\"2026-10-10\",\"fechaFin\":\"2026-10-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams[0].campo").value("fechaFin"));
    }

    // --- 404 ---

    @Test
    void proyectoInexistenteDevuelve404ConElMismoFormato() throws Exception {
        mockMvc.perform(get("/proyectos/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.detail").value("No existe proyecto con id 99"))
                .andExpect(jsonPath("$.instance").value("/proyectos/99"));
    }

    @Test
    void tareaInexistenteDevuelve404EnTodasLasOperaciones() throws Exception {
        String tarea = "{\"titulo\":\"Comprar\",\"prioridad\":\"alta\",\"proyectoId\":1}";
        mockMvc.perform(get("/tareas/99")).andExpect(status().isNotFound());
        mockMvc.perform(put("/tareas/99").contentType(MediaType.APPLICATION_JSON).content(tarea))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.instance").value("/tareas/99"));
        mockMvc.perform(patch("/tareas/99").contentType(MediaType.APPLICATION_JSON).content("{\"completada\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void tareasDeUnProyectoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/proyectos/99/tareas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.instance").value("/proyectos/99/tareas"));
    }

    // --- 409 ---

    @Test
    void nombreDeProyectoDuplicadoDevuelve409() throws Exception {
        crearProyecto();
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"  alpha \"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.instance").value("/proyectos"));
    }

    @Test
    void renombrarUnProyectoConElNombreDeOtroDevuelve409() throws Exception {
        crearProyecto();
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Beta\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/proyectos/2").contentType(MediaType.APPLICATION_JSON).content("{\"nombre\":\"Alpha\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/proyectos/2").contentType(MediaType.APPLICATION_JSON).content(PROYECTO_OK))
                .andExpect(status().isConflict());
    }

    @Test
    void tareaEnProyectoInexistenteDevuelve409() throws Exception {
        mockMvc.perform(post("/tareas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Comprar\",\"prioridad\":\"alta\",\"proyectoId\":99}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("No existe el proyecto 99"));
    }

    @Test
    void tareaEnProyectoInactivoDevuelve409() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Archivado\",\"activo\":false}"))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/tareas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Comprar\",\"prioridad\":\"alta\",\"proyectoId\":1}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("El proyecto 1 está archivado y no admite tareas"));
    }

    @Test
    void conservarElPropioNombreNoEsConflicto() throws Exception {
        crearProyecto();
        mockMvc.perform(put("/proyectos/1").contentType(MediaType.APPLICATION_JSON).content(PROYECTO_OK))
                .andExpect(status().isOk());
    }

    // --- Caminos raros: los lanza Spring antes de llegar a un método nuestro ---

    @Test
    void metodoNoPermitidoDevuelve405ConElMismoFormato() throws Exception {
        mockMvc.perform(delete("/proyectos"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.status").value(405))
                .andExpect(jsonPath("$.instance").value("/proyectos"))
                .andExpect(jsonPath("$.momento").exists());
    }

    @Test
    void contentTypeIncorrectoDevuelve415() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.TEXT_PLAIN).content("hola"))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.instance").value("/proyectos"));
    }

    @Test
    void idQueNoEsNumeroDevuelve400() throws Exception {
        mockMvc.perform(get("/tareas/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.instance").value("/tareas/abc"));
    }

    // --- Correcciones: recorte antes de validar y fechas guardadas ---
    @Test
    void nombreConEspaciosSeRecortaAntesDeValidar() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\" ab \"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalidParams[0].campo").value("nombre"));
    }

    @Test
    void lasFechasDelProyectoSeGuardanYSeDevuelven() throws Exception {
        mockMvc.perform(post("/proyectos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Alpha\",\"fechaInicio\":\"2026-10-01\",\"fechaFin\":\"2026-10-10\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fechaInicio").value("2026-10-01"))
                .andExpect(jsonPath("$.fechaFin").value("2026-10-10"));
        mockMvc.perform(get("/proyectos/1"))
                .andExpect(jsonPath("$.fechaFin").value("2026-10-10"));
    }
}
