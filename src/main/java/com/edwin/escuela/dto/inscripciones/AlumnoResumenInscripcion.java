package com.edwin.escuela.dto.inscripciones;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen de datos del alumno en la inscripción")
public record AlumnoResumenInscripcion(
        String nombre,
        String matricula,
        String email,
        String fechaIngreso
) {}