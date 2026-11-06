package com.edwin.escuela.services;

import com.edwin.escuela.dto.grupos.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.enums.DiaSemana;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.GrupoMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.grupos.GrupoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GrupoServiceTest {
    @Mock AulaRepository aulaRepository;
    @Mock CursoRepository cursoRepository;
    @Mock GrupoRepository grupoRepository;
    @Mock HorarioRepository horarioRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock MaestroRepository maestroRepository;
    @Mock GrupoMapper grupoMapper;
    @InjectMocks GrupoServiceImpl service;


    @Test
    void registrar_datosValidos_registraGrupo() {
        // Arrange
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        Curso curso = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula()));
        GrupoResponse esperado = mock(GrupoResponse.class);
        when(grupoMapper.entidadAResponse(any(Grupo.class))).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        org.mockito.ArgumentCaptor<Grupo> captura = org.mockito.ArgumentCaptor.forClass(Grupo.class);
        verify(grupoRepository).saveAndFlush(captura.capture());
        Grupo guardado = captura.getValue();
        assertThat(guardado.getCurso().getId()).isEqualTo(request.idCurso());
        assertThat(guardado.getMaestro().getId()).isEqualTo(request.idMaestro());
        assertThat(guardado.getAula().getId()).isEqualTo(request.idAula());
        assertThat(guardado.getPeriodo()).isEqualTo(request.periodo());
        verify(grupoMapper).entidadAResponse(guardado);
    }

    @Test
    void registrar_periodoFormatoValido_registraGrupo() {
        // Arrange
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        Curso curso = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula()));
        GrupoResponse esperado = mock(GrupoResponse.class);
        when(grupoMapper.entidadAResponse(any(Grupo.class))).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        org.mockito.ArgumentCaptor<Grupo> captura = org.mockito.ArgumentCaptor.forClass(Grupo.class);
        verify(grupoRepository).saveAndFlush(captura.capture());
        Grupo guardado = captura.getValue();
        assertThat(guardado.getCurso().getId()).isEqualTo(request.idCurso());
        assertThat(guardado.getMaestro().getId()).isEqualTo(request.idMaestro());
        assertThat(guardado.getAula().getId()).isEqualTo(request.idAula());
        assertThat(guardado.getPeriodo()).isEqualTo(request.periodo());
        verify(grupoMapper).entidadAResponse(guardado);
    }

    @Test
    void registrar_combinacionDuplicada_lanzaConflictoException() {
        // Arrange
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        Curso curso = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula()));
        when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(1L,1L,1L,"2026-01")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_cursoInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-01");
        Curso curso = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_grupoExistente_retornaGrupoConHorarios() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        GrupoResponse esperado = mock(GrupoResponse.class);
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange


        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.obtenerPorId(999L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conGrupos_retornaLista() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findAll()).thenReturn(List.of(entidad));
        GrupoResponse esperado = mock(GrupoResponse.class);
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(grupoRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_datosValidos_actualizaGrupo() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-02");
        Curso curso = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula()));
        GrupoResponse esperado = mock(GrupoResponse.class);
        when(grupoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(grupoRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_combinacionDeOtroGrupo_lanzaConflictoException() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        GrupoRequest request = new GrupoRequest(1L,1L,1L,"2026-02");
        Curso curso = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(maestro()));
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(aula()));
        when(grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(1L,1L,1L,"2026-02",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinInscripcionesNiHorarios_eliminaGrupo() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(grupoRepository).delete(entidad);
        verify(grupoRepository).flush();
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(inscripcionRepository.existsByGrupoId(1L)).thenReturn(true);

        // Act
        EntidadRelacionadaException error = assertThrows(EntidadRelacionadaException.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
        verify(grupoRepository,never()).delete(any());
    }

    @Test
    void eliminar_conHorarios_lanzaEntidadRelacionadaException() {
        // Arrange
        Grupo entidad = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(horarioRepository.findByGrupoId(1L)).thenReturn(List.of(horario()));

        // Act
        EntidadRelacionadaException error = assertThrows(EntidadRelacionadaException.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(grupoRepository,never()).saveAndFlush(any());
        verify(grupoRepository,never()).delete(any());
    }

    private Maestro maestro() {
        return Maestro.builder().id(1L).nombre("Laura").apellidoPaterno("Perez").apellidoMaterno("Gomez").email("laura@escuela.com").telefono("5512345678").build();
    }

    private Aula aula() {
        return Aula.builder().id(1L).nombre("Aula 101").capacidad(30).build();
    }

    private Curso curso() {
        return Curso.builder().id(1L).nombre("Matematicas I").descripcion("Fundamentos").creditos(6).build();
    }

    private Grupo grupo() {
        return Grupo.builder().id(1L).curso(curso()).maestro(maestro()).aula(aula()).periodo("2026-01").build();
    }

    private Horario horario() {
        return Horario.builder().id(1L).grupo(grupo()).diaSemana(DiaSemana.LUNES).horaInicio("08:00").horaFin("10:00").build();
    }
}
