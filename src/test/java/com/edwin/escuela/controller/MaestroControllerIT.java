package com.edwin.escuela.controller;

import com.edwin.escuela.entities.*;
import com.edwin.escuela.services.maestros.MaestroService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "server.port=0",
    "spring.datasource.url=jdbc:h2:mem:pruebasMaestro;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false",
    "logging.level.root=ERROR", "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class MaestroControllerIT {
    private static final String RUTA = "/api/maestros";
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @MockitoSpyBean MaestroService service;


    @Test
    void registrar_datosValidos_registraMaestro() throws Exception {
        // Arrange
        String json = requestValido();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void registrar_emailDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        prepararEntidad();
        String json = requestValido();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_telefonoDuplicado_lanzaConflictoException() throws Exception {
        // Arrange
        prepararEntidad();
        String json = requestValido().replace("laura@escuela.com","otra@escuela.com");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_maestroExistente_retornaMaestroConCursos() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Curso materia = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        Aula salon = guardar(Aula.crear("Aula 101",30));
        guardar(Grupo.crear(materia,em.find(Maestro.class,id),salon,"2026-01"));
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        resultado.andExpect(jsonPath("$.cursos[0].nombre").value("Matematicas I"));
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        Long id = 999999L;
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void listar_conMaestros_retornaLista() throws Exception {
        // Arrange
        prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() throws Exception {
        // Arrange
        doThrow(new IllegalStateException("Error interno simulado")).when(service).listar();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA));

        // Assert
        resultado.andExpect(status().is(500))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void actualizar_mismoEmailYTelefonoPropios_noLanzaConflicto() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = requestValido();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
    }

    @Test
    void actualizar_emailDeOtroMaestro_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        guardar(Maestro.crear("Ana","Perez","Gomez","otra@escuela.com","5599999999"));
        String json = requestValido().replace("laura@escuela.com","otra@escuela.com");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_sinGrupos_eliminaMaestro() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(204));
        assertThat(em.find(Maestro.class,id)).isNull();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Curso materia = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        Maestro docente = em.find(Maestro.class,id);
        Aula salon = guardar(Aula.crear("Aula 101",30));
        guardar(Grupo.crear(materia,docente,salon,"2026-01"));
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(409))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_bodyVacio_retorna400() throws Exception {
        // Arrange
        // No requiere registros previos.

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(""));

        // Assert
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    private String requestValido() {
        return "{\"nombre\":\"Laura\",\"apellidoPaterno\":\"Perez\",\"apellidoMaterno\":\"Gomez\",\"email\":\"laura@escuela.com\",\"telefono\":\"5512345678\"}";
    }

    private Long prepararEntidad() {
        return guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678")).getId();
    }

    private <T> T guardar(T entidad) {
        em.persist(entidad);
        em.flush();
        return entidad;
    }
}
