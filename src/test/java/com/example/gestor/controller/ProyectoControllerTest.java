package com.example.gestor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.gestor.memoria.MemoriaProyecto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProyectoControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProyectoController(new MemoriaProyecto())).build();
    }

    @Test
    void patchPermiteActualizarSoloElNombre() throws Exception {
        mockMvc.perform(post("/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Original\",\"descripcion\":\"Base\",\"activo\":true,\"numeroDeIncidencias\":3}"))
                .andExpect(status().isCreated());

        mockMvc.perform(patch("/proyectos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Proyecto modificado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Proyecto modificado"))
                .andExpect(jsonPath("$.descripcion").value("Base"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.numeroDeIncidencias").value(3));
    }

            @Test
            void putDevuelveProyectoResponse() throws Exception {
            mockMvc.perform(post("/proyectos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Original\",\"descripcion\":\"Base\",\"activo\":true,\"numeroDeIncidencias\":3}"))
                .andExpect(status().isCreated());

            mockMvc.perform(put("/proyectos/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Modificado\",\"descripcion\":\"Nueva\",\"activo\":false,\"numeroDeIncidencias\":8}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Modificado"))
                .andExpect(jsonPath("$.descripcion").value("Nueva"))
                .andExpect(jsonPath("$.activo").value(false))
                .andExpect(jsonPath("$.numeroDeIncidencias").value(8));
            }

            @Test
            void getDevuelveProyectoResponse() throws Exception {
            mockMvc.perform(post("/proyectos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"nombre\":\"Consulta\",\"descripcion\":\"Base\",\"activo\":true,\"numeroDeIncidencias\":1}"))
                .andExpect(status().isCreated());

            mockMvc.perform(get("/proyectos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Consulta"))
                .andExpect(jsonPath("$.descripcion").value("Base"))
                .andExpect(jsonPath("$.activo").value(true))
                .andExpect(jsonPath("$.numeroDeIncidencias").value(1));
            }

    @Test
    void postDevuelveBadRequestSiFaltaNombre() throws Exception {
        mockMvc.perform(post("/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"descripcion\":\"Sin nombre\",\"activo\":true,\"numeroDeIncidencias\":2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postDevuelveBadRequestSiNombreEsDemasiadoCorto() throws Exception {
        mockMvc.perform(post("/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"AB\",\"descripcion\":\"Corto\",\"activo\":true,\"numeroDeIncidencias\":2}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void postDevuelveBadRequestSiDescripcionSuperaLongitudMaxima() throws Exception {
        String descripcionLarga = "a".repeat(501);

        mockMvc.perform(post("/proyectos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Proyecto válido\",\"descripcion\":\"" + descripcionLarga + "\",\"activo\":true,\"numeroDeIncidencias\":2}"))
                .andExpect(status().isBadRequest());
    }
}