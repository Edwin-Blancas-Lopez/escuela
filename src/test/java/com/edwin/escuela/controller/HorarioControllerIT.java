package com.edwin.escuela.controller;

import com.edwin.escuela.entities.*;
import com.edwin.escuela.enums.DiaSemana;
import com.edwin.escuela.services.horarios.HorarioService;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "server.port=0",
    "spring.datasource.url=jdbc:h2:mem:pruebasHorario;MODE=Oracle;DB_CLOSE_DELAY=-1",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.open-in-view=false",
    "logging.level.root=ERROR", "spring.main.banner-mode=off"
})
@AutoConfigureMockMvc
@Transactional
class HorarioControllerIT {
    private static final String RUTA = "/api/horarios";
    @Autowired MockMvc mvc;
    @Autowired EntityManager em;
    @MockitoSpyBean HorarioService service;
    Curso curso;
    Maestro maestro;
    Aula aula;
    Grupo grupo;


    @BeforeEach
    void prepararDatos() {
        curso = guardar(Curso.crear("Matematicas I","Fundamentos",6));
        maestro = guardar(Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678"));
        aula = guardar(Aula.crear("Aula 101",30));
        grupo = guardar(Grupo.crear(curso,maestro,aula,"2026-01"));
    }

    @Test
    void registrar_datosValidos_registraHorario() throws Exception {
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
    void registrar_mismoGrupoDatosIgualesExceptoPeriodo_registraExitosamente() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        Grupo siguiente = guardar(Grupo.crear(curso,maestro,aula,"2026-02"));
        String json = horarioJson(siguiente,"08:00","10:00");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(201))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.grupo.periodo").value("2026-02"));
    }

    @Test
    void registrar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        String json = horarioJson(grupo,"10:00","08:00");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_horaInicioIgualAHoraFin_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        String json = horarioJson(grupo,"08:00","08:00");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void registrar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        Curso otroCurso = guardar(Curso.crear("Fisica","Fundamentos",6));
        Grupo otro = guardar(Grupo.crear(otroCurso,maestro,aula,"2026-01"));
        guardarHorario(otro,"08:00","10:00");
        String json = horarioJson(grupo,"09:00","11:00");
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
    void registrar_traslapeEnElMismoGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        guardarHorario(grupo,"08:00","10:00");
        String json = horarioJson(grupo,"09:00","11:00");
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
    void registrar_formatoHoraInvalido_retorna400() throws Exception {
        // Arrange
        String json = horarioJson(grupo,"8:00","10:00");
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
    void registrar_grupoInexistente_lanzaRecursoNoEncontradoException() throws Exception {
        // Arrange
        String json = "{\"idGrupo\":999999,\"dia\":\"LUNES\",\"horaInicio\":\"08:00\",\"horaFin\":\"10:00\"}";
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
    void obtenerPorId_horarioExistente_retornaHorario() throws Exception {
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
    void listar_conHorarios_retornaLista() throws Exception {
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
    void actualizar_datosValidos_actualizaHorario() throws Exception {
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
    void actualizar_mismoGrupoDatosIgualesExceptoPeriodo_actualizaExitosamente() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        Grupo siguiente = guardar(Grupo.crear(curso,maestro,aula,"2026-02"));
        String json = horarioJson(siguiente,"08:00","10:00");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(200))
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$").isNotEmpty());
        resultado.andExpect(jsonPath("$.id").value(id));
        resultado.andExpect(jsonPath("$.grupo.periodo").value("2026-02"));
    }

    @Test
    void actualizar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        String json = horarioJson(grupo,"10:00","08:00");
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(put(RUTA+"/{id}",id).contentType("application/json").content(json));

        // Assert
        resultado.andExpect(status().is(400))
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty());
    }

    @Test
    void actualizar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.find(Horario.class,id).actualizar(grupo,DiaSemana.LUNES,"12:00","13:00");
        Curso otroCurso = guardar(Curso.crear("Fisica","Fundamentos",6));
        Grupo otro = guardar(Grupo.crear(otroCurso,maestro,aula,"2026-01"));
        guardarHorario(otro,"08:00","10:00");
        String json = horarioJson(grupo,"09:00","11:00");
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
    void eliminar_horarioExistente_eliminaHorario() throws Exception {
        // Arrange
        Long id = prepararEntidad();
        em.flush();
        em.clear();

        // Act
        ResultActions resultado = mvc.perform(delete(RUTA+"/{id}",id));

        // Assert
        resultado.andExpect(status().is(204));
        assertThat(em.find(Horario.class,id)).isNull();
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
        ResultActions resultado = mvc.perform(post(RUTA).contentType("application/json").content(requestValido().replace("\"idGrupo\":"+grupo.getId(),"\"idGrupo\":null")));

        // Assert
        resultado.andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.detail").isNotEmpty())
                .andExpect(jsonPath("$.errores").isArray());
    }

    private String requestValido() {
        return horarioJson(grupo,"08:00","10:00");
    }

    private Long prepararEntidad() {
        return guardarHorario(grupo,"08:00","10:00").getId();
    }

    private <T> T guardar(T entidad) {
        em.persist(entidad);
        em.flush();
        return entidad;
    }

    private Horario guardarHorario(Grupo destino,String inicio,String fin) {
        return guardar(Horario.crear(destino,DiaSemana.LUNES,inicio,fin));
    }

    private String horarioJson(Grupo destino,String inicio,String fin) {
        return "{\"idGrupo\":%d,\"dia\":\"LUNES\",\"horaInicio\":\"%s\",\"horaFin\":\"%s\"}".formatted(destino.getId(),inicio,fin);
    }
}
