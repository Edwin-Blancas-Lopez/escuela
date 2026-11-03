package com.edwin.escuela.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class AlumnoTest {

    @Test
    void crear_debeRetornarAlumno_cuandoDatosSonValidos() {
        Alumno alumno = Alumno.crear("María", "Sánchez", "Pérez", "maria@mail.com", "2023001");
        assertThat(alumno).isNotNull();
        assertThat(alumno.getNombre()).isEqualTo("María");
        assertThat(alumno.getFechaIngreso()).isEqualTo(LocalDate.now());
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoMatriculaEsVacia() {
        assertThatThrownBy(() -> Alumno.crear("María", "Sánchez", "Pérez", "maria@mail.com", ""))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("La matrícula es requerida");
    }

    @Test
    void crear_debeLanzarExcepcion_cuandoEmailEsMuyCorto() {
        assertThatThrownBy(() -> Alumno.crear("María", "Sánchez", "Pérez", "m@m.c", "2023001"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("debe tener entre 8 y 100 caracteres");
    }

    @Test
    void actualizar_debeModificarDatos_cuandoDatosSonValidos() {
        Alumno alumno = Alumno.crear("María", "Sánchez", "Pérez", "maria@mail.com", "2023001");
        alumno.actualizar("Luisa", "García", "López", "luisa@mail.com", "2023002");

        assertThat(alumno.getNombre()).isEqualTo("Luisa");
        assertThat(alumno.getMatricula()).isEqualTo("2023002");
    }

    @Test
    void actualizar_debeLanzarExcepcion_cuandoApellidoEsInvalido() {
        Alumno alumno = Alumno.crear("María", "Sánchez", "Pérez", "maria@mail.com", "2023001");

        assertThatThrownBy(() -> alumno.actualizar("Luisa", "", "López", "luisa@mail.com", "2023002"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validarDatos_debePasar_cuandoDatosSonCorrectos() {
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() ->
                Alumno.validarDatos("Pedro", "Infante", "Cruz", "pedro@mail.com", "12345")
        );
    }
}