package com.edwin.escuela.services;

import com.edwin.escuela.dto.horarios.*;
import com.edwin.escuela.entities.*;
import com.edwin.escuela.enums.DiaSemana;
import com.edwin.escuela.exceptions.*;
import com.edwin.escuela.mappers.HorarioMapper;
import com.edwin.escuela.repositories.*;
import com.edwin.escuela.services.horarios.HorarioServiceImpl;
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
class HorarioServiceTest {
    @Mock GrupoRepository grupoRepository;
    @Mock HorarioRepository horarioRepository;
    @Mock HorarioMapper horarioMapper;
    @InjectMocks HorarioServiceImpl service;


    @Test
    void registrar_datosValidos_registraHorario() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));
        HorarioResponse esperado = mock(HorarioResponse.class);
        when(horarioMapper.entidadAResponse(any(Horario.class))).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        org.mockito.ArgumentCaptor<Horario> captura = org.mockito.ArgumentCaptor.forClass(Horario.class);
        verify(horarioRepository).saveAndFlush(captura.capture());
        Horario guardado = captura.getValue();
        assertThat(guardado.getGrupo().getId()).isEqualTo(request.idGrupo());
        assertThat(guardado.getHoraInicio()).isEqualTo(request.horaInicio());
        assertThat(guardado.getHoraFin()).isEqualTo(request.horaFin());
        verify(horarioMapper).entidadAResponse(guardado);
    }

    @Test
    void registrar_mismoGrupoDatosIgualesExceptoPeriodo_registraExitosamente() {
        // Arrange
        HorarioRequest request = new HorarioRequest(2L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        destino.setId(2L);
        destino.setPeriodo("2026-02");
        Horario anterior = horario();
        when(horarioRepository.findByGrupoId(anyLong())).thenAnswer(inv -> java.util.Map.of(1L,List.of(anterior)).getOrDefault(inv.getArgument(0),List.of()));
        when(horarioRepository.findByAulaIdAndPeriodo(eq(1L),anyString())).thenAnswer(inv -> java.util.Map.of("2026-01",List.of(anterior)).getOrDefault(inv.getArgument(1),List.of()));
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(destino));
        HorarioResponse esperado = mock(HorarioResponse.class);
        when(horarioMapper.entidadAResponse(any(Horario.class))).thenReturn(esperado);

        // Act
        var resultado = service.registrar(request);

        // Assert
        verify(horarioRepository).findByAulaIdAndPeriodo(1L,"2026-02");
        assertThat(resultado).isSameAs(esperado);
        org.mockito.ArgumentCaptor<Horario> captura = org.mockito.ArgumentCaptor.forClass(Horario.class);
        verify(horarioRepository).saveAndFlush(captura.capture());
        Horario guardado = captura.getValue();
        assertThat(guardado.getGrupo().getId()).isEqualTo(request.idGrupo());
        assertThat(guardado.getHoraInicio()).isEqualTo(request.horaInicio());
        assertThat(guardado.getHoraFin()).isEqualTo(request.horaFin());
        verify(horarioMapper).entidadAResponse(guardado);
    }

    @Test
    void registrar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"10:00","08:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_horaInicioIgualAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","08:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));
        Grupo ocupado = grupo();
        ocupado.setId(3L);
        Horario existente = Horario.crear(ocupado,DiaSemana.LUNES,"09:00","11:00");
        existente.setId(3L);
        when(horarioRepository.findByAulaIdAndPeriodo(1L,"2026-01")).thenReturn(List.of(existente));

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_traslapeEnElMismoGrupo_lanzaConflictoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));
        when(horarioRepository.findByGrupoId(1L)).thenReturn(List.of(horario()));

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void registrar_grupoInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");
        when(grupoRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.registrar(request));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void obtenerPorId_horarioExistente_retornaHorario() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        HorarioResponse esperado = mock(HorarioResponse.class);
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

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
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void listar_conHorarios_retornaLista() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findAll()).thenReturn(List.of(entidad));
        HorarioResponse esperado = mock(HorarioResponse.class);
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.listar();

        // Assert
        assertThat(resultado).containsExactly(esperado);
    }

    @Test
    void listar_errorDeRepositorio_propagaExcepcion() {
        // Arrange
        IllegalStateException causa = new IllegalStateException("Error de repositorio");
        when(horarioRepository.findAll()).thenThrow(causa);

        // Act
        IllegalStateException error = assertThrows(IllegalStateException.class,service::listar);

        // Assert
        assertThat(error).isSameAs(causa);
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_datosValidos_actualizaHorario() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));
        when(horarioRepository.findByGrupoId(1L)).thenReturn(List.of(entidad));
        when(horarioRepository.findByAulaIdAndPeriodo(1L,"2026-01")).thenReturn(List.of(entidad));
        HorarioResponse esperado = mock(HorarioResponse.class);
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        assertThat(resultado).isSameAs(esperado);
        verify(horarioRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_mismoGrupoDatosIgualesExceptoPeriodo_actualizaExitosamente() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        HorarioRequest request = new HorarioRequest(2L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        destino.setId(2L);
        destino.setPeriodo("2026-02");
        Horario anterior = horario();
        when(horarioRepository.findByGrupoId(anyLong())).thenAnswer(inv -> java.util.Map.of(1L,List.of(anterior)).getOrDefault(inv.getArgument(0),List.of()));
        when(horarioRepository.findByAulaIdAndPeriodo(eq(1L),anyString())).thenAnswer(inv -> java.util.Map.of("2026-01",List.of(anterior)).getOrDefault(inv.getArgument(1),List.of()));
        when(grupoRepository.findById(2L)).thenReturn(Optional.of(destino));
        HorarioResponse esperado = mock(HorarioResponse.class);
        when(horarioMapper.entidadAResponse(entidad)).thenReturn(esperado);

        // Act
        var resultado = service.actualizar(request,1L);

        // Assert
        verify(horarioRepository).findByAulaIdAndPeriodo(1L,"2026-02");
        assertThat(resultado).isSameAs(esperado);
        verify(horarioRepository).saveAndFlush(entidad);
    }

    @Test
    void actualizar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"10:00","08:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_aulaOcupadaPorOtroGrupo_lanzaConflictoException() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");
        Grupo destino = grupo();
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(destino));
        Grupo ocupado = grupo();
        ocupado.setId(3L);
        Horario existente = Horario.crear(ocupado,DiaSemana.LUNES,"09:00","11:00");
        existente.setId(3L);
        when(horarioRepository.findByAulaIdAndPeriodo(1L,"2026-01")).thenReturn(List.of(existente));

        // Act
        ConflictoException error = assertThrows(ConflictoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void actualizar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange
        HorarioRequest request = new HorarioRequest(1L,DiaSemana.LUNES,"08:00","10:00");

        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.actualizar(request,1L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
    }

    @Test
    void eliminar_horarioExistente_eliminaHorario() {
        // Arrange
        Horario entidad = horario();
        when(horarioRepository.findById(1L)).thenReturn(Optional.of(entidad));

        // Act
        service.eliminar(1L);

        // Assert
        verify(horarioRepository).delete(entidad);
        verify(horarioRepository).flush();
    }

    @Test
    void eliminar_idInexistente_lanzaRecursoNoEncontradoException() {
        // Arrange


        // Act
        RecursoNoEncontradoException error = assertThrows(RecursoNoEncontradoException.class,() -> service.eliminar(999L));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
        verify(horarioRepository,never()).saveAndFlush(any());
        verify(horarioRepository,never()).delete(any());
    }

    private Grupo grupo() {
        return Grupo.builder().id(1L).curso(curso()).maestro(maestro()).aula(aula()).periodo("2026-01").build();
    }

    private Horario horario() {
        return Horario.builder().id(1L).grupo(grupo()).diaSemana(DiaSemana.LUNES).horaInicio("08:00").horaFin("10:00").build();
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
