package com.edwin.escuela.dto.datos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Datos deun curso")
public record DatosCurso(
        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        String nombre,

        @Schema(description = "Descripcion del curso", example = "Curso de cálculo integral")
        String descripcion,

        @Schema(description = "Créditos del curso", example = "5")
        String creditos
) {
}
