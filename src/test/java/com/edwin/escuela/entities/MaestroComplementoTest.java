package com.edwin.escuela.entities;

import com.edwin.escuela.exceptions.DatoInvalidoException;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaestroComplementoTest {

    @Test
    void crear_datosConEspacios_retornaMaestroNormalizado() {
        // Arrange
        String nombre = " Laura ";

        // Act
        Maestro maestro = Maestro.crear(nombre," Perez "," Gomez "," LAURA@ESCUELA.COM ","5512345678");

        // Assert
        assertThat(maestro.getNombre()).isEqualTo("Laura");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("Perez");
        assertThat(maestro.getApellidoMaterno()).isEqualTo("Gomez");
        assertThat(maestro.getEmail()).isEqualTo("laura@escuela.com");
    }

    @Test
    void crear_emailVacio_lanzaDatoInvalidoException() {
        // Arrange
        String email = "";

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> Maestro.crear("Laura","Perez","Gomez",email,"5512345678"));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }

    @Test
    void actualizar_telefonoInvalido_lanzaDatoInvalidoException() {
        // Arrange
        Maestro maestro = Maestro.crear("Laura","Perez","Gomez","laura@escuela.com","5512345678");

        // Act
        DatoInvalidoException error = assertThrows(DatoInvalidoException.class,() -> maestro.actualizar("Laura","Perez","Gomez","laura@escuela.com","123"));

        // Assert
        assertThat(error.getMessage()).isNotBlank();
    }
}
