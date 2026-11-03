package com.edwin.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos de calificacion")
public record DatosCalificacion(
        @Schema(description = "Nombre el curso", example = "Matematicas I")
        String curso,

        @Schema(description = "Periodo de la calificacion", example = "2026-09")
        String periodo,

        @Schema(description = "Valor de la calificacion", example = "9.9")
        String calificacion
) {

}
