package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.grupos.GrupoRequest;
import com.edwin.escuela.dto.grupos.GrupoResponse;
import com.edwin.escuela.entities.Grupo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GrupoMapper implements CommonMapper<GrupoRequest, GrupoResponse, Grupo>{
    private final CursoMapper cursoMapper;
    private final MaestroMapper maestroMapper;
    private final AulaMapper aulaMapper;


    @Override
    public Grupo requestAEntidad(GrupoRequest request) {
        return request == null ? null : Grupo.builder().periodo(request.periodo()).build();
    }

    @Override
    public GrupoResponse entidadAResponse(Grupo entidad) {
        if (entidad == null) return null;

        // Mapea los horarios asociados si existen
        List<String> horariosStr = entidad.getHorarios() == null ? List.of() :
                entidad.getHorarios().stream()
                        .map(h -> h.getDiaSemana() + " " + h.getHoraInicio() + " - " + h.getHoraFin())
                        .toList();

        return new GrupoResponse(
                entidad.getId(),
                cursoMapper.entidadAResponse(entidad.getCurso()),
                maestroMapper.entidadAResponse(entidad.getMaestro()),
                aulaMapper.entidadAResponse(entidad.getAula()),
                horariosStr,
                entidad.getPeriodo()
        );
    }
}
