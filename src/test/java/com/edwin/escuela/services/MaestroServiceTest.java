package com.edwin.escuela.services;

import com.edwin.escuela.dto.mestros.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.MaestroMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.maestros.MaestroServiceImpl;
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
class MaestroServiceTest {
    @Mock GrupoRepository grupoRepository;
    @Mock MaestroRepository maestroRepository;
    @Mock MaestroMapper maestroMapper;
    @InjectMocks MaestroServiceImpl service;


    @Test
    void registrar_datosValidos_registraMaestro() {
        // Arrange
        MaestroRequest request = new MaestroRequest("Laura","Perez","Gomez","laura@escuela.com","5512345678");
        Maestro entidad = maestro();
        when(maestroMapper.requestAEntidad(request)).thenReturn(entidad);
        MaestroResponse esperado = mock(MaestroResponse.class);
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(maestroRepository).saveAndFlush(entidad);
    }

    @Test
    void registrar_emailDuplicado_lanzaConflictoException() {
        // Arrange
        MaestroRequest request = new MaestroRequest("Laura","Perez","Gomez","laura@escuela.com","5512345678");
        when(maestroRepository.existsByEmailIgnoreCase("laura@escuela.com")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_telefonoDuplicado_lanzaConflictoException() {
        // Arrange
        MaestroRequest request = new MaestroRequest("Laura","Perez","Gomez","laura@escuela.com","5512345678");
        when(maestroRepository.existsByTelefono("5512345678")).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_maestroExistente_retornaMaestroConCursos() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        MaestroResponse esperado = mock(MaestroResponse.class);
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

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
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conMaestros_retornaLista() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findAll()).thenReturn(List.of(entidad));
        MaestroResponse esperado = mock(MaestroResponse.class);
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(maestroRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_mismoEmailYTelefonoPropios_noLanzaConflicto() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        MaestroRequest request = new MaestroRequest("Laura","Perez","Gomez","laura@escuela.com","5512345678");
        MaestroResponse esperado = mock(MaestroResponse.class);
        when(maestroMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(maestroRepository).saveAndFlush(entidad);
        verify(maestroRepository).existsByEmailIgnoreCaseAndIdNot("laura@escuela.com",1L);
        verify(maestroRepository).existsByTelefonoAndIdNot("5512345678",1L);
    }

    @Test
    void actualizar_emailDeOtroMaestro_lanzaConflictoException() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        MaestroRequest request = new MaestroRequest("Laura","Perez","Gomez","laura@escuela.com","5512345678");
        when(maestroRepository.existsByEmailIgnoreCaseAndIdNot("laura@escuela.com",1L)).thenReturn(true);

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_sinGrupos_eliminaMaestro() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(maestroRepository).delete(entidad);
        verify(maestroRepository).flush();
    }

    @Test
    void eliminar_conGrupos_lanzaEntidadRelacionadaException() {
        // Arrange
        Maestro entidad = maestro();
        when(maestroRepository.findById(1L)).thenReturn(Optional.of(entidad));
        when(grupoRepository.existsByMaestroId(1L)).thenReturn(true);

        // Act
        EntidadRelacionadaException error = assertThrows(EntidadRelacionadaException.class,() -> service.eliminar(1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(maestroRepository,never()).saveAndFlush(any());
        verify(maestroRepository,never()).delete(any());
    }

    private Maestro maestro() {
        return Maestro.builder().id(1L).nombre("Laura").apellidoPaterno("Perez").apellidoMaterno("Gomez").email("laura@escuela.com").telefono("5512345678").build();
    }
}
