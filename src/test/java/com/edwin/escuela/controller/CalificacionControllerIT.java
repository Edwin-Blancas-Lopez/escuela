package com.edwin.escuela.controller;

import com.edwin.escuela.entities.*;
import com.edwin.escuela.services.calificaciones.CalificacionService;
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
    "spring.datasource.url=jdbc:h2:mem:pruebasCalificacion;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false",
    "logging.level.root=ERROR", "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class CalificacionControllerIT {
    private static final String RUTA = "/api/calificaciones";
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @MockitoSpyBean CalificacionService service;
    Curso curso;
    Maestro maestro;
    Aula aula;
    Grupo grupo;
    Alumno alumno;
    Inscripcion inscripcion;


    @BeforeEach
    void prepararDatos() {
        curso = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        maestro = guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678"));
        aula = guardar(Aula.crear("Aula 101",30));
        grupo = guardar(Grupo.crear(curso,maestro,aula,"2026-01"));
        alumno = guardarAlumno();
        inscripcion = guardar(Inscripcion.crear(alumno,grupo));
    }

    @Test
    void registrar_datosValidos_registraCalificacion() throws Exception {
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
    void registrar_inscripcionYaCalificada_lanzaConflictoException() throws Exception {
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
    void registrar_inscripcionInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        String json = "{\"idInscripcion\":999999,\"calificacion\":8.5}";
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
    void registrar_calificacionNula_retorna400() throws Exception {
        // Arrange
        String json = "{\"idInscripcion\":%d,\"calificacion\":null}".formatted(inscripcion.getId());
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.errores").isArray());
    }

    @Test
    void obtenerPorId_calificacionExistente_retornaCalificacion() throws Exception {
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
    void listar_conCalificaciones_retornaLista() throws Exception {
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
    void actualizar_valorValido_actualizaCalificacion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = requestValido().replace("8.5","9.0");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        resultado.andExpect(jsonPath("$.calificacion").value(9));
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        Long id = 999999L;
        String json = requestValido();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(404))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void eliminar_calificacionExistente_eliminaCalificacion() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(204));
        assertThat(em.find(Calificacion.class,id)).isNull();
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        Long id = 999999L;
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(404))
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
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(requestValido().replace("\"idInscripcion\":"+inscripcion.getId(),"\"idInscripcion\":null")));

        // Assert
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.errores").isArray());
    }

    private String requestValido() {
        return "{\"idInscripcion\":%d,\"calificacion\":8.5}".formatted(inscripcion.getId());
    }

    private Long prepararEntidad() {
        return guardar(Calificacion.crear(inscripcion,new BigDecimal("8.5"))).getIdCalificacion();
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
