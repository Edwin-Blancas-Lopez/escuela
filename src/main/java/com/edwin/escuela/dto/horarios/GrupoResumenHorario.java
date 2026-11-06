package com.edwin.escuela.dto.horarios;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resumen del grupo para el horario")
public record GrupoResumenHorario(
        String curso,
        String maestro,
        String aula,
        String periodo
) {}