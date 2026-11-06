package com.edwin.escuela.dto.horarios;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Información detallada de un horario")
public record HorarioResponse(
        @Schema(description = "ID del horario", example = "1")
        Long id,

        @Schema(description = "Resumen del grupo asociado")
        GrupoResumenHorario grupo,

        @Schema(description = "Descripción textual del horario", example = "Lunes 08:00 10:00")
        String horario
) {}