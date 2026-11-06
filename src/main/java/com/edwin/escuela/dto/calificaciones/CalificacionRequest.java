package com.edwin.escuela.dto.calificaciones;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

@Schema(description = "Datos necesarios para registrar o actualizar una calificación")
public record CalificacionRequest(
        @Schema(description = "ID de la inscripción asociada", example = "1")
        @NotNull(message = "La inscripción es requerida")
        @Positive(message = "El ID de la inscripción debe ser positivo")
        Long idInscripcion,

        @Schema(description = "Calificación numérica (0 a 10)", example = "8.5")
        @NotNull(message = "La calificación es requerida")
        @Min(value = 0, message = "La calificación mínima es 0")
        @Max(value = 10, message = "La calificación máxima es 10")
        BigDecimal calificacion
) {

}
