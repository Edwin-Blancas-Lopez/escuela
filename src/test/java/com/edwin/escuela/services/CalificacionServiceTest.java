package com.edwin.escuela.services;

import com.edwin.escuela.dto.calificaciones.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.CalificacionMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.calificaciones.CalificacionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {
    @Mock CalificacionRepository calificacionRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock CalificacionMapper calificacionMapper;
    @InjectMocks CalificacionServiceImpl service;


    @Test
    void registrar_datosValidos_registraCalificacion() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(1L,new BigDecimal("8.5"));
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion()));
        CalificacionResponse esperado = mock(CalificacionResponse.class);
        when(calificacionMapper.entidadAResponse(any(Calificacion.class))).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        org.mockito.ArgumentCaptor<Calificacion> captura = org.mockito.ArgumentCaptor.forClass(Calificacion.class);
        verify(calificacionRepository).saveAndFlush(captura.capture());
        Calificacion guardado = captura.getValue();
        assertThat(guardado.getInscripcion().getId()).isEqualTo(request.idInscripcion());
        assertThat(guardado.getCalificacion()).isEqualByComparingTo(request.calificacion());
        verify(calificacionMapper).entidadAResponse(guardado);
    }

    @Test
    void registrar_inscripcionYaCalificada_lanzaConflictoException() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(1L,new BigDecimal("8.5"));
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion()));
        when(calificacionRepository.existsByInscripcionId(1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_inscripcionInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(1L,new BigDecimal("8.5"));
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_calificacionExistente_retornaCalificacion() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CalificacionResponse esperado = mock(CalificacionResponse.class);
        when(calificacionMapper.entidadAResponse(entidad)).thenReturn(esperado);

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
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conCalificaciones_retornaLista() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findAll()).thenReturn(List.of(entidad));
        CalificacionResponse esperado = mock(CalificacionResponse.class);
        when(calificacionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(calificacionRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_valorValido_actualizaCalificacion() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CalificacionRequest request = new CalificacionRequest(1L,new BigDecimal("8.5"));
        when(inscripcionRepository.findById(1L)).thenReturn(Optional.of(inscripcion()));
        CalificacionResponse esperado = mock(CalificacionResponse.class);
        when(calificacionMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(calificacionRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        CalificacionRequest request = new CalificacionRequest(1L,new BigDecimal("8.5"));

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(calificacionRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_calificacionExistente_eliminaCalificacion() {
        // Arrange
        Calificacion entidad = calificacion();
        when(calificacionRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(calificacionRepository).delete(entidad);
        verify(calificacionRepository).flush();
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange


        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.eliminar(999L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(calificacionRepository,never()).saveAndFlush(any());
        verify(calificacionRepository,never()).delete(any());
    }

    private Inscripcion inscripcion() {
        return Inscripcion.builder().id(1L).alumno(alumno()).grupo(grupo()).build();
    }

    private Calificacion calificacion() {
        return Calificacion.builder().idCalificacion(1L).inscripcion(inscripcion()).calificacion(new BigDecimal("8.5")).fechaRegistro(LocalDate.now()).build();
    }

    private Alumno alumno() {
        return Alumno.builder().id(1L).nombre("Carlos").apellidoPaterno("Perez").apellidoMaterno("Gomez").email("carlos@escuela.com").matricula("CAPEG02601").build();
    }

    private Grupo grupo() {
        return Grupo.builder().id(1L).curso(curso()).maestro(maestro()).aula(aula()).periodo("2026-01").build();
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
