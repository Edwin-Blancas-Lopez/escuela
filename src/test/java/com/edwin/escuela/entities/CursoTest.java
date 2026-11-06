package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CursoTest {

    @Test
    void crear_debeRetornarCurso_cuandoDatosSonValidos() {
        Curso curso = Curso.crear("Matemáticas", "Álgebra lineal", 8);
        assertThat(curso).isNotNull();
        assertThat(curso.getNombre()).isEqualTo("Matemáticas");
        assertThat(curso.getCreditos()).isEqualTo(8);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoNombreEsVacio() {
        assertThatThrownBy(() -> Curso.crear("", "Descripción", 5))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void actualizar_debeModificarCurso_cuandoDatosSonValidos() {
        Curso curso = Curso.crear("Química", "Química orgánica", 6);
        curso.actualizar("Química Avanzada", "Termodinámica", 10);

        assertThat(curso.getNombre()).isEqualTo("Química Avanzada");
        assertThat(curso.getCreditos()).isEqualTo(10);
    }

    @Test
    void validarDatos_debePasar_cuandoCursoEsValido() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Curso.validarDatos("Historia", "Historia universal", 4)
        );
    }
}