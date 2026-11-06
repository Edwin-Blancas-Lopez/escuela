package com.edwin.escuela.entities;

import com.edwin.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AulaComplementoTest {

    @Test
    void actualizar_nombreVacio_lanzaDatoInvalidoException() {
        // Arrange
        Aula entidad = Aula.crear("Aula 101",30);

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> entidad.actualizar("",30));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }

    @Test
    void cambioEnDatos_nombreDistinto_retornaTrue() {
        // Arrange
        Aula entidad = Aula.crear("Aula 101",30);

        // Act
        boolean resultado = entidad.cambioEnDatos("Aula 202");

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void cambioEnDatos_nombreVacio_lanzaDatoInvalidoException() {
        // Arrange
        Aula entidad = Aula.crear("Aula 101",30);

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> entidad.cambioEnDatos(""));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }
}
