package com.edwin.escuela.entities;

import com.edwin.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GrupoComplementoTest {

    @Test
    void crear_periodoNulo_lanzaDatoInvalidoException() {
        // Arrange
        Curso curso = curso();
        Maestro maestro = maestro();
        Aula aula = aula();

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> Grupo.crear(curso,maestro,aula,null));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }

    @Test
    void actualizar_periodoVacio_lanzaDatoInvalidoException() {
        // Arrange
        Grupo entidad = grupo();

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> entidad.actualizar(entidad.getCurso(),entidad.getMaestro(),entidad.getAula(),""));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
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
}
