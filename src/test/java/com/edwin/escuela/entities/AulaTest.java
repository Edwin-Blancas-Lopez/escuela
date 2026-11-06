package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AulaTest {

    @Test
    void crear_debeRetornarAula_cuandoDatosSonValidos() {
        Aula aula = Aula.crear("Laboratorio 1", 30);
        assertThat(aula).isNotNull();
        assertThat(aula.getNombre()).isEqualTo("Laboratorio 1");
        assertThat(aula.getCapacidad()).isEqualTo(30);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreEsVacio() {
        assertThatThrownBy(() -> Aula.crear("", 25))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void actualizar_debeModificarDatos_cuandoDatosSonValidos() {
        Aula aula = Aula.crear("Aula 101", 20);
        aula.actualizar("Aula Magna", 100);

        assertThat(aula.getNombre()).isEqualTo("Aula Magna");
        assertThat(aula.getCapacidad()).isEqualTo(100);
    }

    @Test
    void validarDatos_debePasar_cuandoAulaEsValida() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Aula.validarDatos("Sala Audiovisual", 50)
        );
    }
}