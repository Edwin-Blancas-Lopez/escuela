package com.edwin.escuela.dto.cursos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un curso")
public record CursoRequest(
        @Schema(description = "Nombre del curso", example = "Matemáticas I")
        @NotBlank(message = "El nombre del curso es requerido")
        @Size(min = 1, max = 100, message = "El nombre del curso debe tener entre 1 y 100 caracteres")
        String nombre,

        @Schema(description = "Descripción del curso", example = "Fundamentos matemáticos para nivel básico")
        @NotBlank(message = "La descripción del curso es requerida")
        @Size(min = 1, max = 200, message = "La descripción debe tener entre 1 y 200 caracteres")
        String descripcion,

        @Schema(description = "Créditos del curso", example = "6")
        @NotNull(message = "Los créditos son requeridos")
        @Positive(message = "Los créditos deben ser positivos")
        Integer creditos
) {
}