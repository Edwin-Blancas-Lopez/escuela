package com.edwin.escuela.dto.grupos;

import com.edwin.escuela.dto.aulas.AulaResponse;
import com.edwin.escuela.dto.cursos.CursoResponse;
import com.edwin.escuela.dto.mestros.MaestroResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Información detallada de un grupo")
public record GrupoResponse(
        @Schema(description = "ID del grupo", example = "1")
        Long id,

        @Schema(description = "Información del curso")
        CursoResponse curso,

        @Schema(description = "Información del maestro")
        MaestroResponse maestro,

        @Schema(description = "Información del aula")
        AulaResponse aula,

        @Schema(description = "Lista de horarios asignados al grupo", example = "[\"Lunes 08:00 - 10:00\"]")
        List<String> horarios,

        @Schema(description = "Periodo académico", example = "2026-01")
        String periodo
) {}