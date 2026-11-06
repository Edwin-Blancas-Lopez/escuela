package com.edwin.escuela.controller;

import com.edwin.escuela.dto.alumnos.AlumnoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
        "server.port=0",
        "spring.datasource.url=jdbc:h2:mem:pruebasAlumno;MODE=Oracle;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.open-in-view=false",
        "logging.level.root=ERROR",
        "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class AlumnoControllerTest {

    private static final String RUTA = "/api/alumnos";

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void registrar_nombreVacio_retorna400() throws Exception {

        AlumnoRequest request =
                new AlumnoRequest("", "Pérez", "Gómez");

        mvc.perform(post(RUTA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").exists())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void obtenerPorId_idInexistente_retorna404() throws Exception {

        mvc.perform(get(RUTA + "/{id}", 9999))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.detail").exists());
    }

    @Test
    void listar_conAlumnos_retorna200() throws Exception {

        mvc.perform(get(RUTA))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}