package com.edwin.escuela.dto.grupos;

import com.edwin.escuela.dto.aulas.AulaResponse;
import com.edwin.escuela.dto.aulas.AulaResumenResponse;
import com.edwin.escuela.dto.cursos.CursoResponse;
import com.edwin.escuela.dto.cursos.CursoResumenResponse;
import com.edwin.escuela.dto.mestros.MaestroResponse;
import com.edwin.escuela.dto.mestros.MaestroResumenResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Información detallada de un grupo")
public record GrupoResponse(
        @Schema(description = "ID del grupo", example = "1")
        Long id,

        @Schema(description = "Información del curso")
        CursoResumenResponse curso,

        @Schema(description = "Información del maestro")
        MaestroResumenResponse maestro,

        @Schema(description = "Información del aula")
        AulaResumenResponse aula,

        @Schema(description = "Lista de horarios asignados al grupo", example = "[\"Lunes 08:00 - 10:00\"]")
        List<String> horarios,

        @Schema(description = "Periodo académico", example = "2026-01")
        String periodo
) {}