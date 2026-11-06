package com.edwin.escuela.services;

import com.edwin.escuela.dto.inscripciones.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.InscripcionMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.inscripciones.InscripcionServiceImpl;
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
class InscripcionServiceTest {
    @Mock AlumnoRepository alumnoRepository;
    @Mock CalificacionRepository calificacionRepository;
    @Mock GrupoRepository grupoRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock InscripcionMapper inscripcionMapper;
    @InjectMocks InscripcionServiceImpl service;


    @Test
    void registrar_datosValidos_registraInscripcion() {
        // Arrange
        InscripcionRequest request = new InscripcionRequest(1L,1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno()));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo()));
        InscripcionResponse esperado = mock(InscripcionResponse.class);
        when(inscripcionMapper.entidadAResponse(any(Inscripcion.class))).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        org.mockito.ArgumentCaptor<Inscripcion> captura = org.mockito.ArgumentCaptor.forClass(Inscripcion.class);
        verify(inscripcionRepository).saveAndFlush(captura.capture());
        Inscripcion guardado = captura.getValue();
        assertThat(guardado.getAlumno().getId()).isEqualTo(request.idAlumno());
        assertThat(guardado.getGrupo().getId()).isEqualTo(request.idGrupo());
        assertThat(guardado.getCalificacion()).isNull();
        verify(inscripcionMapper).entidadAResponse(guardado);
    }

    @Test
    void registrar_alumnoYGrupoDuplicados_lanzaConflictoException() {
        // Arrange
        InscripcionRequest request = new InscripcionRequest(1L,1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno()));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo()));
        when(inscripcionRepository.existsByAlumnoIdAndGrupoId(1L,1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_alumnoInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        InscripcionRequest request = new InscripcionRequest(1L,1L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_inscripcionExistente_retornaInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(entidad));
        InscripcionResponse esperado = mock(InscripcionResponse.class);
        when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

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
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conInscripciones_retornaLista() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findAll()).thenReturn(List.of(entidad));
        InscripcionResponse esperado = mock(InscripcionResponse.class);
        when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(inscripcionRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_otroGrupoValido_actualizaInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(entidad));
        InscripcionRequest request = new InscripcionRequest(1L,2L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno()));
        Grupo destino = grupo();
        destino.setId(2L);
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(destino));
        InscripcionResponse esperado = mock(InscripcionResponse.class);
        when(inscripcionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(inscripcionRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_combinacionYaExistente_lanzaConflictoException() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(entidad));
        InscripcionRequest request = new InscripcionRequest(1L,2L);
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(alumno()));
        Grupo destino = grupo();
        destino.setId(2L);
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(destino));
        when(inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(1L,2L,1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinCalificacion_eliminaInscripcion() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(inscripcionRepository).delete(entidad);
        verify(inscripcionRepository).flush();
    }

    @Test
    void eliminar_conCalificacion_lanzaEntidadRelacionadaException() {
        // Arrange
        Inscripcion entidad = inscripcion();
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(calificacionRepository.existsByInscripcionId(1L)).thenReturn(true);

        // Act
        EntidadRelacionadaException error = assertThrows(EntidadRelacionadaException.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(inscripcionRepository,never()).saveAndFlush(any());
        verify(inscripcionRepository,never()).delete(any());
    }

    private Alumno alumno() {
        return Alumno.builder().id(1L).nombre("Carlos").apellidoPaterno("Perez").apellidoMaterno("Gomez").email("carlos@escuela.com").matricula("CAPEG02601").build();
    }

    private Grupo grupo() {
        return Grupo.builder().id(1L).curso(curso()).maestro(maestro()).aula(aula()).periodo("2026-01").build();
    }

    private Inscripcion inscripcion() {
        return Inscripcion.builder().id(1L).alumno(alumno()).grupo(grupo()).build();
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
}
