package com.edwin.escuela.dto.grupos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

@Schema(description = "Datos necesarios para registrar o actualizar un grupo")
public record GrupoRequest(
        @Schema(description = "ID del curso", example = "2")
        @NotNull(message = "El ID del curso es requerido")
        @Positive(message = "El ID del curso debe ser positivo")
        Long idCurso,

        @Schema(description = "ID del maestro", example = "7")
        @NotNull(message = "El ID del maestro es requerido")
        @Positive(message = "El ID del maestro debe ser positivo")
        Long idMaestro,

        @Schema(description = "ID del aula", example = "3")
        @NotNull(message = "El ID del aula es requerido")
        @Positive(message = "El ID del aula debe ser positivo")
        Long idAula,

        @Schema(description = "Periodo académico en formato yyyy-MM", example = "2026-01")
        @NotBlank(message = "El periodo es requerido")
        @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "El periodo debe tener el formato yyyy-MM, por ejemplo 2026-01")
        String periodo
) {
}
