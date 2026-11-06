package com.edwin.escuela.dto.aulas;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un aula")
public record AulaResponse(
        @Schema(description = "ID del aula", example = "1")
        Long id,

        @Schema(description = "Nombre aula", example = "Aula 101")
        String nombreAula,

        @Schema(description = "Capacidad del aula", example = "30")
        Integer capacidad
) {
}
