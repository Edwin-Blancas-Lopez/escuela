package com.edwin.escuela.dto.calificaciones;

import com.edwin.escuela.dto.inscripciones.InscripcionResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Información detallada de una calificación")
public record CalificacionResponse(
        @Schema(description = "ID de la calificación", example = "1")
        Long id,

        @Schema(description = "Información de la inscripción asociada")
        InscripcionResponse inscripcion,

        @Schema(description = "Valor de la calificación", example = "9.0")
        BigDecimal calificacion,

        @Schema(description = "Fecha de registro de la calificación", example = "11/02/2026")
        String fechaRegistro
) {
}
