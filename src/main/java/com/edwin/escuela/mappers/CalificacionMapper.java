package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.calificaciones.CalificacionRequest;
import com.edwin.escuela.dto.calificaciones.CalificacionResponse;
import com.edwin.escuela.entities.Calificacion;
import com.edwin.escuela.utils.StringCustomUtils;
import lombok.Builder;
import org.springframework.stereotype.Component;

@Builder
@Component
public class CalificacionMapper implements CommonMapper<CalificacionRequest, CalificacionResponse, Calificacion>{
    private final InscripcionMapper inscripcionMapper;

    @Override
    public Calificacion requestAEntidad(CalificacionRequest request) {
        return null;
    }

    @Override
    public CalificacionResponse entidadAResponse(Calificacion entidad) {
        if (entidad == null) return null;

        return new CalificacionResponse(
                entidad.getIdCalificacion(),
                inscripcionMapper.entidadAResponse(entidad.getInscripcion()),
                entidad.getCalificacion(),
                StringCustomUtils.localDateAString(entidad.getFechaRegistro())
        );
    }
}
