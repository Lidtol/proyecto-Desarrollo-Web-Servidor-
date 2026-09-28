package com.example.gestor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProyectoControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ProyectoController()).build();
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
}