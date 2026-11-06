package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.grupos.GrupoResumenInscripcion;
import com.edwin.escuela.dto.inscripciones.AlumnoResumenInscripcion;
import com.edwin.escuela.dto.inscripciones.InscripcionRequest;
import com.edwin.escuela.dto.inscripciones.InscripcionResponse;
import com.edwin.escuela.entities.Inscripcion;
import com.edwin.escuela.utils.StringCustomUtils;
import lombok.Builder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Builder
@Component
public class InscripcionMapper implements CommonMapper<InscripcionRequest, InscripcionResponse, Inscripcion>{
    @Override
    public Inscripcion requestAEntidad(InscripcionRequest request) {
        return null;
    }

    @Override
    public InscripcionResponse entidadAResponse(Inscripcion entidad) {
        if (entidad == null) return null;

        var alumnoDto = new AlumnoResumenInscripcion(
                entidad.getAlumno().obtenerNombreCompleto(),
                entidad.getAlumno().getMatricula(),
                entidad.getAlumno().getEmail(),
                StringCustomUtils.localDateAString(entidad.getAlumno().getFechaIngreso())
        );

        var grupoDto = new GrupoResumenInscripcion(
                entidad.getGrupo().getCurso().getNombre(),
                entidad.getGrupo().getMaestro().obtenerNombreCompleto(),
                entidad.getGrupo().getAula().getNombre(),
                entidad.getGrupo().getPeriodo()
        );
        BigDecimal califValor = (entidad.getCalificacion() != null) ? entidad.getCalificacion().getCalificacion() : null;

        return new InscripcionResponse(
                entidad.getId(),
                alumnoDto,
                grupoDto,
                califValor,
                StringCustomUtils.localDateAString(entidad.getFechaInscripcion())
        );
    }
}
