package com.edwin.escuela.services;

import com.edwin.escuela.dto.alumnos.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.AlumnoMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.alumnos.AlumnoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceComplementoTest {
    @Mock AlumnoRepository alumnoRepository;
    @Mock InscripcionRepository inscripcionRepository;
    @Mock AlumnoMapper alumnoMapper;
    @InjectMocks AlumnoServiceImpl service;


    @Test
    void registrar_datosValidos_generaMatriculaYEmail() {
        // Arrange
        AlumnoRequest request = new AlumnoRequest("Carlos","Perez","Gomez");
        Alumno entidad = alumno();
        when(alumnoRepository.generarEmail("Carlos","Perez","Gomez")).thenReturn("carlos@escuela.com");
        when(alumnoRepository.generarMatricula("Carlos","Perez","Gomez")).thenReturn("CAPEG02601");
        when(alumnoMapper.requestAEntidad(request,"carlos@escuela.com","CAPEG02601")).thenReturn(entidad);
        AlumnoResponse esperado = mock(AlumnoResponse.class);
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(alumnoRepository).saveAndFlush(entidad);
    }

    @Test
    void obtenerPorId_sinCalificaciones_retornaPromedioCero() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AlumnoResponse esperado = mock(AlumnoResponse.class);
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(entidad.calcularPromedio()).isEqualByComparingTo("0.00");
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void obtenerPorId_conCalificacionesNulas_ignoraNulosEnPromedio() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        entidad.getInscripciones().add(Inscripcion.builder().calificacion(Calificacion.builder().calificacion(new BigDecimal("8")).build()).build());
        entidad.getInscripciones().add(Inscripcion.builder().build());
        entidad.getInscripciones().add(Inscripcion.builder().calificacion(Calificacion.builder().build()).build());
        AlumnoResponse esperado = mock(AlumnoResponse.class);
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.obtenerPorId(1L);

        // Assert
        assertThat(entidad.calcularPromedio()).isEqualByComparingTo("8.00");
        assertThat(resultado).isSameAs(esperado);
    }

    @Test
    void actualizar_cambiaSoloApellidoMaterno_regeneraMatriculaYEmail() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AlumnoRequest request = new AlumnoRequest("Carlos","Perez","Lopez");
        when(alumnoRepository.generarEmail("Carlos","Perez","Lopez")).thenReturn("carlos.lopez@escuela.com");
        when(alumnoRepository.generarMatricula("Carlos","Perez","Lopez")).thenReturn("CAPEL02601");
        AlumnoResponse esperado = mock(AlumnoResponse.class);
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(entidad.getApellidoMaterno()).isEqualTo("Lopez");
        assertThat(entidad.getEmail()).isEqualTo("carlos.lopez@escuela.com");
        assertThat(entidad.getMatricula()).isEqualTo("CAPEL02601");
        assertThat(resultado).isSameAs(esperado);
        verify(alumnoRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_sinCambiosEnDatos_noRegeneraNiGuarda() {
        // Arrange
        Alumno entidad = alumno();
        when(alumnoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AlumnoRequest request = new AlumnoRequest("Carlos","Perez","Gomez");
        AlumnoResponse esperado = mock(AlumnoResponse.class);
        when(alumnoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(alumnoRepository,never()).generarEmail(any(),any(),any());
        verify(alumnoRepository,never()).generarMatricula(any(),any(),any());
        verify(alumnoRepository,never()).saveAndFlush(any());
    }

    private Alumno alumno() {
        return Alumno.builder().id(1L).nombre("Carlos").apellidoPaterno("Perez").apellidoMaterno("Gomez").email("carlos@escuela.com").matricula("CAPEG02601").build();
    }
}
