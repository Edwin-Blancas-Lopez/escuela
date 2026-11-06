package com.edwin.escuela.controller;

import com.edwin.escuela.entities.*;
import com.edwin.escuela.repositories.AlumnoRepository;
import com.edwin.escuela.services.alumnos.AlumnoService;
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
    "spring.datasource.url=jdbc:h2:mem:pruebasAlumno;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false",
    "logging.level.root=ERROR", "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class AlumnoControllerIT {
    private static final String RUTA = "/api/alumnos";
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @MockitoSpyBean AlumnoService service;
    @MockitoSpyBean AlumnoRepository alumnoRepository;


    @BeforeEach
    void prepararDatos() {
        doAnswer(inv -> "alumno." + Integer.toUnsignedString(java.util.Objects.hash(inv.getArguments())) + "@escuela.com")
                .when(alumnoRepository).generarEmail(anyString(),anyString(),anyString());
        doAnswer(inv -> String.format("%010d",Integer.toUnsignedLong(java.util.Objects.hash(inv.getArguments()))))
                .when(alumnoRepository).generarMatricula(anyString(),anyString(),anyString());
    }

    @Test
    void registrar_datosValidos_generaMatriculaYEmail() throws Exception {
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
        resultado.andExpect(jsonPath("$.matricula",hasLength(10))).andExpect(jsonPath("$.email",containsString("@")));
    }

    @Test
    void registrar_nombreVacio_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        String json = "{\"nombre\":\"\",\"apellidoPaterno\":\"Perez\",\"apellidoMaterno\":\"Gomez\"}";
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
    void obtenerPorId_alumnoExistente_retornaAlumnoConPromedio() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Alumno inscrito = em.find(Alumno.class,id);
        Curso materia = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        Maestro docente = guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678"));
        Aula salon = guardar(Aula.crear("Aula 101",30));
        Grupo destino = guardar(Grupo.crear(materia,docente,salon,"2026-01"));
        Inscripcion cursada = guardar(Inscripcion.crear(inscrito,destino));
        guardar(Calificacion.crear(cursada,new BigDecimal("8")));
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        resultado.andExpect(jsonPath("$.promedio").value(8)).andExpect(jsonPath("$.calificaciones").isNotEmpty());
    }

    @Test
    void obtenerPorId_sinCalificaciones_retornaPromedioCero() throws Exception {
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
        resultado.andExpect(jsonPath("$.promedio").value(0));
    }

    @Test
    void obtenerPorId_conCalificacionesNulas_ignoraNulosEnPromedio() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Alumno inscrito = em.find(Alumno.class,id);
        Curso materia = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        Maestro docente = guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678"));
        Aula salon = guardar(Aula.crear("Aula 101",30));
        Grupo destino = guardar(Grupo.crear(materia,docente,salon,"2026-01"));
        Inscripcion cursada = guardar(Inscripcion.crear(inscrito,destino));
        guardar(Calificacion.crear(cursada,new BigDecimal("8")));
        Grupo sinNota = guardar(Grupo.crear(materia,docente,salon,"2026-02"));
        guardar(Inscripcion.crear(inscrito,sinNota));
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(get(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        resultado.andExpect(jsonPath("$.promedio").value(8)).andExpect(jsonPath("$.calificaciones").isNotEmpty());
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
    void listar_conAlumnos_retornaLista() throws Exception {
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
    void actualizar_cambiaNombre_regeneraMatriculaYEmail() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Alumno anterior = em.find(Alumno.class,id);
        String matriculaAnterior = anterior.getMatricula();
        String emailAnterior = anterior.getEmail();
        String json = requestValido().replace("Carlos","Juan");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        assertThat(em.find(Alumno.class,id).getMatricula()).isNotEqualTo(matriculaAnterior);
        assertThat(em.find(Alumno.class,id).getEmail()).isNotEqualTo(emailAnterior);
    }

    @Test
    void actualizar_cambiaSoloApellidoMaterno_regeneraMatriculaYEmail() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Alumno anterior = em.find(Alumno.class,id);
        String matriculaAnterior = anterior.getMatricula();
        String emailAnterior = anterior.getEmail();
        String json = requestValido().replace("Gomez","Lopez");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        assertThat(em.find(Alumno.class,id).getMatricula()).isNotEqualTo(matriculaAnterior);
        assertThat(em.find(Alumno.class,id).getEmail()).isNotEqualTo(emailAnterior);
    }

    @Test
    void actualizar_sinCambiosEnDatos_noRegeneraNiGuarda() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Alumno anterior = em.find(Alumno.class,id);
        String matriculaAnterior = anterior.getMatricula();
        String emailAnterior = anterior.getEmail();
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
        assertThat(em.find(Alumno.class,id).getMatricula()).isEqualTo(matriculaAnterior);
        assertThat(em.find(Alumno.class,id).getEmail()).isEqualTo(emailAnterior);
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
    void eliminar_sinInscripciones_eliminaAlumno() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(204));
        assertThat(em.find(Alumno.class,id)).isNull();
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Curso materia = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        Maestro docente = guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678"));
        Aula salon = guardar(Aula.crear("Aula 101",30));
        Grupo destino = guardar(Grupo.crear(materia,docente,salon,"2026-01"));
        guardar(Inscripcion.crear(em.find(Alumno.class,id),destino));
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
        return "{\"nombre\":\"Carlos\",\"apellidoPaterno\":\"Perez\",\"apellidoMaterno\":\"Gomez\"}";
    }

    private Long prepararEntidad() {
        return guardarAlumno().getId();
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
