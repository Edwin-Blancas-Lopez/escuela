package com.edwin.escuela.services;

import com.edwin.escuela.dto.aulas.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.AulaMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.aulas.AulaServiceImpl;
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
class AulaServiceTest {
    @Mock AulaRepository aulaRepository;
    @Mock GrupoRepository grupoRepository;
    @Mock AulaMapper aulaMapper;
    @InjectMocks AulaServiceImpl service;


    @Test
    void registrar_datosValidos_registraAula() {
        // Arrange
        AulaRequest request = new AulaRequest("Aula 101",30);
        Aula entidad = aula();
        when(aulaMapper.requestAEntidad(request)).thenReturn(entidad);
        AulaResponse esperado = mock(AulaResponse.class);
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(aulaRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_nombreDuplicado_lanzaConflictoException() {
        // Arrange
        AulaRequest request = new AulaRequest("Aula 101",30);
        when(aulaRepository.existsByNombreIgnoreCase("Aula 101")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(aulaRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_aulaExistente_retornaAula() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AulaResponse esperado = mock(AulaResponse.class);
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

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
        verify(aulaRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conAulas_retornaLista() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findAll()).thenReturn(List.of(entidad));
        AulaResponse esperado = mock(AulaResponse.class);
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(aulaRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(aulaRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_mismoNombrePropio_noLanzaConflicto() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AulaRequest request = new AulaRequest("Aula 101",30);
        AulaResponse esperado = mock(AulaResponse.class);
        when(aulaMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(aulaRepository).saveAndFlush(entidad);
        verify(aulaRepository).existsByNombreIgnoreCaseAndIdNot(request.nombre(),1L);
    }

    @Test
    void actualizar_nombreDeOtraAula_lanzaConflictoException() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        AulaRequest request = new AulaRequest("Aula 101",30);
        when(aulaRepository.existsByNombreIgnoreCaseAndIdNot("Aula 101",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(aulaRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinGrupos_eliminaAula() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(aulaRepository).delete(entidad);
        verify(aulaRepository).flush();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() {
        // Arrange
        Aula entidad = aula();
        when(aulaRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.existsByAulaId(1L)).thenReturn(true);

        // Act
        EntidadRelacionadaException error = assertThrows(EntidadRelacionadaException.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(aulaRepository,never()).saveAndFlush(any());
        verify(aulaRepository,never()).delete(any());
    }

    private Aula aula() {
        return Aula.builder().id(1L).nombre("Aula 101").capacidad(30).build();
    }
}
