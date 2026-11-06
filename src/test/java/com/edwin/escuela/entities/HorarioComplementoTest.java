package com.edwin.escuela.entities;

import com.edwin.escuela.enums.DiaSemana;
import com.edwin.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HorarioComplementoTest {

    @Test
    void crear_horaInicioMayorOIgualQueHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        Grupo grupo = grupo();

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> Horario.crear(grupo,DiaSemana.LUNES,"08:00","08:00"));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }

    @Test
    void actualizar_horaInicioMayorAHoraFin_lanzaDatoInvalidoException() {
        // Arrange
        Horario entidad = horario();

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> entidad.actualizar(entidad.getGrupo(),DiaSemana.LUNES,"10:00","08:00"));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
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
