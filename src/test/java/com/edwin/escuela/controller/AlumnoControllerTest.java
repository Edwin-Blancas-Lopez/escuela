package com.edwin.escuela.controller;

import com.edwin.escuela.dto.alumnos.AlumnoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AlumnoController.class)
class AlumnoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registrar_datosValidos_retorna201() throws Exception {
        // Arrange
        AlumnoRequest request = new AlumnoRequest("Carlos", "Pérez", "Gómez");

        // Act & Assert
        mockMvc.perform(post("/api/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void registrar_nombreVacio_retorna400() throws Exception {
        // Arrange
        AlumnoRequest request = new AlumnoRequest("", "Pérez", "Gómez");

        // Act & Assert
        mockMvc.perform(post("/api/alumnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void obtenerPorId_idNegativo_retorna400() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/alumnos/{id}", -1))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.errores[0]").value("El ID debe ser positivo"));
    }

    @Test
    void obtenerPorId_idInexistente_retorna404() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/alumnos/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void listar_conAlumnos_retorna200() throws Exception {
        // Arrange & Act & Assert
        mockMvc.perform(get("/api/alumnos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}