package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MaestroTest {

    @Test
    void crear_debeRetornarMaestro_cuandoDatosSonValidos() {
        Maestro maestro = Maestro.crear("Juan", "Pérez", "Gómez", "juan@mail.com", "4431234567");
        assertThat(maestro).isNotNull();
        assertThat(maestro.getNombre()).isEqualTo("Juan");
        assertThat(maestro.getEmail()).isEqualTo("juan@mail.com");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreEsVacio() {
        assertThatThrownBy(() -> Maestro.crear("", "Pérez", "Gómez", "juan@mail.com", "4431234567"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("El nombre es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoTelefonoEsInvalido() {
        assertThatThrownBy(() -> Maestro.crear("Juan", "Pérez", "Gómez", "juan@mail.com", "123"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("exactamente 10 caracteres");
    }

    @Test
    void actualizar_debeModificarDatos_cuandoDatosSonValidos() {
        Maestro maestro = Maestro.crear("Juan", "Pérez", "Gómez", "juan@mail.com", "4431234567");
        maestro.actualizar("Carlos", "López", "Ruiz", "carlos@mail.com", "4439876543");

        assertThat(maestro.getNombre()).isEqualTo("Carlos");
        assertThat(maestro.getApellidoPaterno()).isEqualTo("López");
        assertThat(maestro.getEmail()).isEqualTo("carlos@mail.com");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoEmailEsInvalido() {
        Maestro maestro = Maestro.crear("Juan", "Pérez", "Gómez", "juan@mail.com", "4431234567");

        assertThatThrownBy(() -> maestro.actualizar("Carlos", "López", "Ruiz", "abc", "4439876543"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("debe tener entre 8 y 100 caracteres");
    }

    @Test
    void validarDatos_debePasar_cuandoTodosLosCamposSonCorrectos() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Maestro.validarDatos("Ana", "Torres", "Mendoza", "ana@mail.com", "4435554433")
        );
    }
}