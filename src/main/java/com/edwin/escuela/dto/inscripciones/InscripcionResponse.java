package com.edwin.escuela.dto.inscripciones;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Información detallada de una inscripción")
public record InscripcionResponse(
        @Schema(description = "ID de la inscripción", example = "1")
        Long id,

        @Schema(description = "Información resumida del alumno")
        AlumnoResumenInscripcion alumno,

        @Schema(description = "Información resumida del grupo")
        com.edwin.escuela.dto.grupos.GrupoResumenInscripcion grupo,

        @Schema(description = "Calificación obtenida (si existe)", example = "9.0")
        BigDecimal calificacion,

        @Schema(description = "Fecha en que se realizó la inscripción", example = "11/02/2026")
        String fechaInscripcion
) {}

