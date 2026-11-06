package com.edwin.escuela.services;

import com.edwin.escuela.dto.cursos.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.CursoMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.cursos.CursoServiceImpl;
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
class CursoServiceTest {
    @Mock CursoRepository cursoRepository;
    @Mock GrupoRepository grupoRepository;
    @Mock CursoMapper cursoMapper;
    @InjectMocks CursoServiceImpl service;


    @Test
    void registrar_datosValidos_registraCurso() {
        // Arrange
        CursoRequest request = new CursoRequest("Matematicas I","Fundamentos",6);
        Curso entidad = curso();
        when(cursoMapper.requestAEntidad(request)).thenReturn(entidad);
        CursoResponse esperado = mock(CursoResponse.class);
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(cursoRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_nombreDuplicado_lanzaConflictoException() {
        // Arrange
        CursoRequest request = new CursoRequest("Matematicas I","Fundamentos",6);
        when(cursoRepository.existsByNombreIgnoreCase("Matematicas I")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(cursoRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_cursoExistente_retornaCurso() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CursoResponse esperado = mock(CursoResponse.class);
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

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
        verify(cursoRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conCursos_retornaLista() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findAll()).thenReturn(List.of(entidad));
        CursoResponse esperado = mock(CursoResponse.class);
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(cursoRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(cursoRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_mismoNombrePropio_noLanzaConflicto() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CursoRequest request = new CursoRequest("Matematicas I","Fundamentos",6);
        CursoResponse esperado = mock(CursoResponse.class);
        when(cursoMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(cursoRepository).saveAndFlush(entidad);
        verify(cursoRepository).existsByNombreIgnoreCaseAndIdNot(request.nombre(),1L);
    }

    @Test
    void actualizar_nombreDeOtroCurso_lanzaConflictoException() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));
        CursoRequest request = new CursoRequest("Matematicas I","Fundamentos",6);
        when(cursoRepository.existsByNombreIgnoreCaseAndIdNot("Matematicas I",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(cursoRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinGrupos_eliminaCurso() {
        // Arrange
        Curso entidad = curso();
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(cursoRepository).delete(entidad);
        verify(cursoRepository).flush();
    }

    private Curso curso() {
        return Curso.builder().id(1L).nombre("Matematicas I").descripcion("Fundamentos").creditos(6).build();
    }
}
