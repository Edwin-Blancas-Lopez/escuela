package com.edwin.escuela.services.alumnos;

import com.edwin.escuela.dto.alumnos.AlumnoRequest;
import com.edwin.escuela.dto.alumnos.AlumnoResponse;
import com.edwin.escuela.entities.Alumno;
import com.edwin.escuela.exceptions.EntidadRelacionadaException;
import com.edwin.escuela.exceptions.RecursoNoEncontradoException;
import com.edwin.escuela.mappers.AlumnoMapper;
import com.edwin.escuela.repositories.AlumnoRepository;
import com.edwin.escuela.repositories.InscripcionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock
    AlumnoRepository alumnoRepository;

   // private final AlumnoMapper alumnoMapper = new AlumnoMapper();
    @Spy
    private AlumnoMapper alumnoMapper = new AlumnoMapper();

    @Mock
    InscripcionRepository inscripcionRepository;

    @InjectMocks
    AlumnoServiceImpl alumnoService;

    @Test
    void registrar_nombreVacio_lanzaDatoInvalidoException() {
        // Arrange
        AlumnoRequest request = new AlumnoRequest("", "Pérez", "Gómez");

        // Act & Assert
        assertThrows(RuntimeException.class, () -> alumnoService.registrar(request));
        verify(alumnoRepository, never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_alumnoExistente_retornaAlumnoConPromedio() {
        // Arrange
        Long id = 1L;
        Alumno alumno = Alumno.crear("Carlos", "Pérez", "Gómez");
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));
        when(alumnoMapper.entidadAResponse(alumno)).thenReturn(mock(AlumnoResponse.class));

        // Act
        AlumnoResponse response = alumnoService.obtenerPorId(id);

        // Assert
        assertNotNull(response);
        verify(alumnoRepository, times(1)).findById(id);
    }

    @Test
    void obtenerPorId_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        Long id = 99L;
        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> alumnoService.obtenerPorId(id));
    }

    @Test
    void listar_conAlumnos_retornaLista() {
        // Arrange
        when(alumnoRepository.findAll()).thenReturn(List.of(Alumno.crear("Carlos", "Pérez", "Gómez")));
        when(alumnoMapper.entidadAResponse(any())).thenReturn(mock(AlumnoResponse.class));

        // Act
        List<AlumnoResponse> lista = alumnoService.listar();

        // Assert
        assertFalse(lista.isEmpty());
        verify(alumnoRepository, times(1)).findAll();
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        when(alumnoRepository.findAll()).thenThrow(new RuntimeException("Error BD"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> alumnoService.listar());
    }

    @Test
    void actualizar_cambiaNombre_regeneraMatriculaYEmail() {
        // Arrange
        Long id = 1L;
        Alumno alumno = Alumno.crear("Carlos", "Pérez", "Gómez");
        AlumnoRequest request = new AlumnoRequest("Juan", "Pérez", "Gómez");

        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));
        when(alumnoRepository.generarEmail(any(), any(), any())).thenReturn("juan.perez@alumnos.com");
        when(alumnoRepository.generarMatricula(any(), any(), any())).thenReturn("A2026002");

        // Act
        alumnoService.actualizar(request, id);

        // Assert
        verify(alumnoRepository, times(1)).saveAndFlush(any());
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        Long id = 99L;
        AlumnoRequest request = new AlumnoRequest("Juan", "Pérez", "Gómez");
        when(alumnoRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RecursoNoEncontradoException.class, () -> alumnoService.actualizar(request, id));
        verify(alumnoRepository, never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinInscripciones_eliminaAlumno() {
        // Arrange
        Long id = 1L;
        Alumno alumno = Alumno.crear("Carlos", "Pérez", "Gómez");
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));
        when(inscripcionRepository.existsByAlumnoId(id)).thenReturn(false);

        // Act
        alumnoService.eliminar(id);

        // Assert
        verify(alumnoRepository, times(1)).delete(alumno);
    }

    @Test
    void eliminar_conInscripciones_lanzaEntidadRelacionadaException() {
        // Arrange
        Long id = 1L;
        Alumno alumno = Alumno.crear("Carlos", "Pérez", "Gómez");
        when(alumnoRepository.findById(id)).thenReturn(Optional.of(alumno));
        when(inscripcionRepository.existsByAlumnoId(id)).thenReturn(true);

        // Act & Assert
        assertThrows(EntidadRelacionadaException.class, () -> alumnoService.eliminar(id));
        verify(alumnoRepository, never()).delete(any());
    }
}