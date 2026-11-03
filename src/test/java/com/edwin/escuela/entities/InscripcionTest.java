package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class InscripcionTest {

    @Test
    void crear_debeRetornarInscripcion_cuandoDatosSonValidos() {
        Alumno alumno = new Alumno();
        Grupo grupo = new Grupo();

        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);
        assertThat(inscripcion).isNotNull();
        assertThat(inscripcion.getAlumno()).isEqualTo(alumno);
        assertThat(inscripcion.getFechaInscripcion()).isEqualTo(LocalDate.now());
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoAlumnoEsNulo() {
        Grupo grupo = new Grupo();

        assertThatThrownBy(() -> Inscripcion.crear(null, grupo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El alumno es requerido");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoGrupoEsNulo() {
        Alumno alumno = new Alumno();

        assertThatThrownBy(() -> Inscripcion.crear(alumno, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El grupo es requerido");
    }

    @Test
    void actualizar_debeModificarInscripcion_cuandoDatosSonValidos() {
        Alumno alumno1 = new Alumno();
        Alumno alumno2 = new Alumno();
        Grupo grupo = new Grupo();

        Inscripcion inscripcion = Inscripcion.crear(alumno1, grupo);
        inscripcion.actualizar(alumno2, grupo);

        assertThat(inscripcion.getAlumno()).isEqualTo(alumno2);
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoAlumnoEsNulo() {
        Alumno alumno = new Alumno();
        Grupo grupo = new Grupo();

        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);

        assertThatThrownBy(() -> inscripcion.actualizar(null, grupo))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("El alumno es requerido");
    }

    @Test
    void validarDatos_debePasar_cuandoInscripcionEsValida() {
        Alumno alumno = new Alumno();
        Grupo grupo = new Grupo();

        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Inscripcion.validarDatos(alumno, grupo)
        );
    }
}