package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CalificacionTest {

    @Test
    void crear_debeRetornarCalificacion_cuandoDatosSonValidos() {
        Inscripcion inscripcion = new Inscripcion();
        Calificacion calificacion = Calificacion.crear(inscripcion, new BigDecimal("9.5"));

        assertThat(calificacion).isNotNull();
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("9.5");
        assertThat(calificacion.getInscripcion()).isEqualTo(inscripcion);
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoCalificacionEsMayorA10() {
        Inscripcion inscripcion = new Inscripcion();
        assertThatThrownBy(() -> Calificacion.crear(inscripcion, new BigDecimal("10.5")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("debe estar entre 0.0 y 10.0");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoInscripcionEsNula() {
        assertThatThrownBy(() -> Calificacion.crear(null, new BigDecimal("8.0")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("inscripción asociada es requerida");
    }

    @Test
    void actualizar_debeModificarCalificacion_cuandoEsValida() {
        Inscripcion inscripcion = new Inscripcion();
        Calificacion calificacion = Calificacion.crear(inscripcion, new BigDecimal("7.0"));

        calificacion.actualizar(inscripcion, new BigDecimal("9.0"));
        assertThat(calificacion.getCalificacion()).isEqualByComparingTo("9.0");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoCalificacionEsNegativa() {
        Inscripcion inscripcion = new Inscripcion();
        Calificacion calificacion = Calificacion.crear(inscripcion, new BigDecimal("7.0"));

        assertThatThrownBy(() -> calificacion.actualizar(inscripcion, new BigDecimal("-1.0")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void validarDatos_debePasar_cuandoCalificacionEstaEnRango() {
        Inscripcion inscripcion = new Inscripcion();
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Calificacion.validarDatos(inscripcion, new BigDecimal("5.5"))
        );
    }
}