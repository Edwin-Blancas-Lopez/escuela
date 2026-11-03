package com.edwin.escuela.dto.alumnos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Datos necesarios para registrar o actualizar un alumno")
public record AlumnoRequest(
        @Schema(description = "Nombre del alumno", example = "Carlos")
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 1, max = 50, message = "El nombre debe tener entre 1 y 50 caracteres")
        String nombre,

        @Schema(description = "Apellido paterno del alumno", example = "Pérez")
        @NotBlank(message = "El apellido paterno es requerido")
        @Size(min = 1, max = 50, message = "El apellido paterno debe tener entre 1 y 50 caracteres")
        String apellidoPaterno,

        @Schema(description = "Apellido materno del alumno", example = "Gómez")
        @NotBlank(message = "El apellido materno es requerido")
        @Size(min = 1, max = 50, message = "El apellido materno debe tener entre 1 y 50 caracteres")
        String apellidoMaterno

      /*  @Schema(description = "Email del alumno", example = "carlos.perez@test.com")
        @NotBlank(message = "El email es requerido")
        @Size(min = 8, max = 100, message = "El email debe tener entre 8 y 100 caracteres")
        @Email(message = "El email debe tener un formato válido (ejemplo@dominio.com)")
        String email,

        @Schema(description = "Matrícula del alumno", example = "2023001234")
        @NotBlank(message = "La matrícula es requerida")
        @Size(min = 1, max = 10, message = "La matrícula debe tener entre 1 y 10 caracteres")
        String matricula*/
) {
}