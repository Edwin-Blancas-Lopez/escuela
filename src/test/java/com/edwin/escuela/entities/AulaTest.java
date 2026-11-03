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
    void crear_debeLanzarExcepcion_cuandoCapacidadEsCeroO_Negativa() {
        assertThatThrownBy(() -> Aula.crear("Aula 101", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("capacidad es requerida y debe ser mayor a 0");
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
    void actualizar_debeLanzarExcepcion_cuandoCapacidadEsNula() {
        Aula aula = Aula.crear("Aula 101", 20);

        assertThatThrownBy(() -> aula.actualizar("Aula Magna", null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validarDatos_debePasar_cuandoAulaEsValida() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Aula.validarDatos("Sala Audiovisual", 50)
        );
    }
}