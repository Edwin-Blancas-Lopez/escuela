package com.edwin.escuela.dto.aulas;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un aula")
public record AulaRequest(
        @Schema(description = "Nombre del aula", example = "Aula")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 30, message = "El nombre debe tener entre 1 y 30 caracteres")
        String nombre,

        @Schema(description = "Capacidad del aula", example = "20")
        @NotNull(message = "La cantidad es requerida")
        @Positive(message = "La cantidad debe ser positiva")
        Integer capacidad
) {
}
