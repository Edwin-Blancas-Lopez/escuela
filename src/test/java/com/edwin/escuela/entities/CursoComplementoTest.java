package com.edwin.escuela.entities;

import com.edwin.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CursoComplementoTest {

    @Test
    void actualizar_nombreVacio_lanzaDatoInvalidoException() {
        // Arrange
        Curso entidad = Curso.crear("Matematicas I","Fundamentos",6);

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> entidad.actualizar("","Fundamentos",6));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }

    @Test
    void cambioEnDatos_nombreDistinto_retornaTrue() {
        // Arrange
        Curso entidad = Curso.crear("Matematicas I","Fundamentos",6);

        // Act
        boolean resultado = entidad.cambioEnDatos("Fisica","Fundamentos",6);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void cambioEnDatos_nombreVacio_lanzaDatoInvalidoException() {
        // Arrange
        Curso entidad = Curso.crear("Matematicas I","Fundamentos",6);

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> entidad.cambioEnDatos("","Fundamentos",6));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }
}
