package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class GrupoTest {

    @Test
    void crear_debeRetornarGrupo_cuandoDatosSonValidos() {
        Curso curso = new Curso();
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        Grupo grupo = Grupo.crear(curso, maestro, aula, "2026-1");
        assertThat(grupo).isNotNull();
        assertThat(grupo.getPeriodo()).isEqualTo("2026-1");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoCursoEsNulo() {
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        assertThatThrownBy(() -> Grupo.crear(null, maestro, aula, "2026-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El curso es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoMaestroEsNulo() {
        Curso curso = new Curso();
        Aula aula = new Aula();

        assertThatThrownBy(() -> Grupo.crear(curso, null, aula, "2026-1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El maestro es requerido");
    }

    @Test
    void actualizar_debeModificarGrupo_cuandoDatosSonValidos() {
        Curso curso = new Curso();
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        Grupo grupo = Grupo.crear(curso, maestro, aula, "2026-1");
        grupo.actualizar(curso, maestro, aula, "2026-2");

        assertThat(grupo.getPeriodo()).isEqualTo("2026-2");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoAulaEsNula() {
        Curso curso = new Curso();
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        Grupo grupo = Grupo.crear(curso, maestro, aula, "2026-1");

        assertThatThrownBy(() -> grupo.actualizar(curso, maestro, null, "2026-2"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El aula es requerida");
    }

    @Test
    void validarDatos_debePasar_cuandoGrupoEsValido() {
        Curso curso = new Curso();
        Maestro maestro = new Maestro();
        Aula aula = new Aula();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Grupo.validarDatos(curso, maestro, aula, "Verano")
        );
    }
}