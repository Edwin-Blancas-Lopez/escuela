package com.edwin.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información de un curso")
public record CursoResponse(
        @Schema(description = "ID del curso", example = "1")
        Long id,

        @Schema(description = "Nombre del curso", example = "Sistemas")
        String nombreCurso,

        @Schema(description = "Descripcion del curso", example = "Todo sobre programacion")
        String descripcion,

        @Schema(description = "Creditos para el curso", example = "5")
        Integer creditos
) {
}
