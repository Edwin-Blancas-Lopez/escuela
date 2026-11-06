package com.edwin.escuela.dto.alumnos;

import com.edwin.escuela.dto.datos.DatosCalificacion;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Schema(description = "Información de un alumno")
public record AlumnoResponse(
        @Schema(description = "ID del alumno", example = "1")
        Long id,

        @Schema(description = "Nombre completo o de pila del alumno", example = "Carlos Pérez Gómez")
        String nombreCompleto,

        @Schema(description = "Email del alumno", example = "carlos.perez@test.com")
        String email,

        @Schema(description = "Matrícula única del alumno", example = "2023001234")
        String matricula,

        @Schema(description = "Fecha de ingreso del alumno a la institución", example = "2026-01-15")
        String fechaIngreso,

        @Schema(description = "Datos de las calificaciones del alumno")
        List<DatosCalificacion> calificaciones,

        @Schema(description = "Promedio del alumno", example = "9.9")
        BigDecimal promedio

        ) {
}