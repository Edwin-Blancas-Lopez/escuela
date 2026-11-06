package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.horarios.GrupoResumenHorario;
import com.edwin.escuela.dto.horarios.HorarioRequest;
import com.edwin.escuela.dto.horarios.HorarioResponse;
import com.edwin.escuela.entities.Horario;
import org.springframework.stereotype.Component;

@Component
public class HorarioMapper implements CommonMapper<HorarioRequest, HorarioResponse, Horario>{

    @Override
    public Horario requestAEntidad(HorarioRequest request) {
        return null;
    }

    @Override
    public HorarioResponse entidadAResponse(Horario entidad) {
        if (entidad == null) return null;

        var grupoDto = new GrupoResumenHorario(
                entidad.getGrupo().getCurso().getNombre(),
                entidad.getGrupo().getMaestro().obtenerNombreCompleto(),
                entidad.getGrupo().getAula().getNombre(),
                entidad.getGrupo().getPeriodo()
        );

        String formatoHorario = entidad.getDiaSemana() + " " + entidad.getHoraInicio() + " " + entidad.getHoraFin();

        return new HorarioResponse(
                entidad.getId(),
                grupoDto,
                formatoHorario
        );
    }
}
