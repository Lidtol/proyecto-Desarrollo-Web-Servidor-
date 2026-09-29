package com.example.gestor.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.example.gestor.memoria.MemoriaProyecto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TareaControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TareaController(new MemoriaProyecto())).build();
    }

    @Test
    void patchPermiteActualizarSoloElTitulo() throws Exception {
        mockMvc.perform(post("/tareas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Original\",\"prioridad\":\"alta\",\"completada\":false}"))
            .andExpect(status().isCreated());

        mockMvc.perform(patch("/tareas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Título modificado\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.titulo").value("Título modificado"))
                .andExpect(jsonPath("$.prioridad").value("alta"))
                .andExpect(jsonPath("$.completada").value(false));
    }
}