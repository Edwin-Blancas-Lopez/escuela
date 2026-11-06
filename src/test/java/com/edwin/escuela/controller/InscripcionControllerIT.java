package com.edwin.escuela.controller;

import com.edwin.escuela.entities.*;
import com.edwin.escuela.services.inscripciones.InscripcionService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "server.port=0",
    "spring.datasource.url=jdbc:h2:mem:pruebasInscripcion;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false",
    "logging.level.root=ERROR", "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class InscripcionControllerIT {
    private static final String RUTA = "/api/inscripciones";
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @MockitoSpyBean InscripcionService service;
    Curso curso;
    Maestro maestro;
    Aula aula;
    Grupo grupo;
    Alumno alumno;


    @BeforeEach
    void prepararDatos() {
        curso = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        maestro = guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678"));
        aula = guardar(Aula.crear("Aula 101",30));
        grupo = guardar(Grupo.crear(curso,maestro,aula,"2026-01"));
        alumno = guardarAlumno();
    }

    @Test
    void registrar_datosValidos_registraInscripcion() throws Exception {
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
        resultado.andExpect(jsonPath("$.calificacion").value(nullValue()));
    }

    @Test
    void registrar_alumnoYGrupoDuplicados_lanzaConflictoException() throws Exception {
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
    void registrar_alumnoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        String json = "{\"idAlumno\":999999,\"idGrupo\":%d}".formatted(grupo.getId());
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void obtenerPorId_inscripcionExistente_retornaInscripcion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
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
    void listar_conInscripciones_retornaLista() throws Exception {
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
    void actualizar_otroGrupoValido_actualizaInscripcion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Grupo otro = guardar(Grupo.crear(curso,maestro,aula,"2026-02"));
        String json = "{\"idAlumno\":%d,\"idGrupo\":%d}".formatted(alumno.getId(),otro.getId());
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
    void actualizar_combinacionYaExistente_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Grupo otro = guardar(Grupo.crear(curso,maestro,aula,"2026-02"));
        guardar(Inscripcion.crear(alumno,otro));
        String json = "{\"idAlumno\":%d,\"idGrupo\":%d}".formatted(alumno.getId(),otro.getId());
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
    void eliminar_sinCalificacion_eliminaInscripcion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(204));
        assertThat(em.find(Inscripcion.class,id)).isNull();
    }

    @Test
    void eliminar_conCalificacion_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        guardar(Calificacion.crear(em.find(Inscripcion.class,id),new BigDecimal("8")));
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

    @Test
    void registrar_idNulo_retorna400() throws Exception {
        // Arrange
        // No requiere registros previos.

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(requestValido().replace("\"idAlumno\":"+alumno.getId(),"\"idAlumno\":null")));

        // Assert
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.errores").isArray());
    }

    private String requestValido() {
        return "{\"idAlumno\":%d,\"idGrupo\":%d}".formatted(alumno.getId(),grupo.getId());
    }

    private Long prepararEntidad() {
        return guardar(Inscripcion.crear(alumno,grupo)).getId();
    }

    private <T> T guardar(T entidad) {
        em.persist(entidad);
        em.flush();
        return entidad;
    }

    private Alumno guardarAlumno() {
        Alumno nuevo = Alumno.crear("Carlos","Perez","Gomez");
        nuevo.asignarDatosAcademicos("carlos@escuela.com","CAPEG02601");
        return guardar(nuevo);
    }
}
