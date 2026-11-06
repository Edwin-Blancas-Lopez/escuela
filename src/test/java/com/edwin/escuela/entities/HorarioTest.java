package com.edwin.escuela.entities;

import com.edwin.escuela.enums.DiaSemana;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HorarioTest {

    @Test
    void crear_debeRetornarHorario_cuandoDatosSonValidos() {
        Grupo grupo = new Grupo();
        Horario horario = Horario.crear(grupo, DiaSemana.LUNES, "08:00", "10:00");

        assertThat(horario).isNotNull();
        assertThat(horario.getDiaSemana()).isEqualTo(DiaSemana.LUNES);
        assertThat(horario.getHoraInicio()).isEqualTo("08:00");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoGrupoEsNulo() {
        assertThatThrownBy(() -> Horario.crear(null, DiaSemana.MARTES, "08:00", "10:00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El grupo es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoDiaSemanaEsNulo() {
        Grupo grupo = new Grupo();
        assertThatThrownBy(() -> Horario.crear(grupo, null, "08:00", "10:00"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El día de la semana es requerido");
    }

    @Test
    void actualizar_debeModificarHorario_cuandoDatosSonValidos() {
        Grupo grupo = new Grupo();
        Horario horario = Horario.crear(grupo, DiaSemana.LUNES, "08:00", "10:00");

        horario.actualizar(grupo, DiaSemana.MIERCOLES, "10:00", "12:00");
        assertThat(horario.getDiaSemana()).isEqualTo(DiaSemana.MIERCOLES);
        assertThat(horario.getHoraInicio()).isEqualTo("10:00");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoHoraInicioEsInvalida() {
        Grupo grupo = new Grupo();
        Horario horario = Horario.crear(grupo, DiaSemana.LUNES, "08:00", "10:00");

        assertThatThrownBy(() -> horario.actualizar(grupo, DiaSemana.LUNES, "8:00", "10:00"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validarDatos_debePasar_cuandoHorarioEsValido() {
        Grupo grupo = new Grupo();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Horario.validarDatos(grupo, DiaSemana.VIERNES, "14:00", "16:00")
        );
    }
}
