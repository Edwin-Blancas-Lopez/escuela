package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.aulas.AulaResumenResponse;
import com.edwin.escuela.dto.cursos.CursoResumenResponse;
import com.edwin.escuela.dto.grupos.GrupoRequest;
import com.edwin.escuela.dto.grupos.GrupoResponse;
import com.edwin.escuela.dto.mestros.MaestroResumenResponse;
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

        // 1. Mapear curso a su versión resumen (sin ID)
        CursoResumenResponse cursoResumen = entidad.getCurso() == null ? null :
                new CursoResumenResponse(
                        entidad.getCurso().getNombre(),
                        entidad.getCurso().getDescripcion(),
                        entidad.getCurso().getCreditos()
                );

        // 2. Mapear maestro a su versión resumen (sin ID ni lista de cursos)
        MaestroResumenResponse maestroResumen = entidad.getMaestro() == null ? null :
                new MaestroResumenResponse(
                        entidad.getMaestro().obtenerNombreCompleto(),
                        entidad.getMaestro().getEmail(),
                        entidad.getMaestro().getTelefono()
                );

        // 3. Mapear aula a su versión resumen (sin ID)
        AulaResumenResponse aulaResumen = entidad.getAula() == null ? null :
                new AulaResumenResponse(
                        entidad.getAula().getNombre(),
                        entidad.getAula().getCapacidad()
                );

        // 4. Formatear los horarios con el día capitalizado (Ej: "Lunes 08:00 - 10:00")
        List<String> horariosStr = entidad.getHorarios() == null ? List.of() :
                entidad.getHorarios().stream()
                        .map(h -> {
                            String diaName = h.getDiaSemana().name(); // Ej: LUNES
                            String diaCapitalizado = diaName.charAt(0) + diaName.substring(1).toLowerCase(); // Lunes
                            return diaCapitalizado + " " + h.getHoraInicio() + " - " + h.getHoraFin();
                        })
                        .toList();

        return new GrupoResponse(
                entidad.getId(),
                cursoResumen,
                maestroResumen,
                aulaResumen,
                horariosStr,
                entidad.getPeriodo()
        );
    }
   /* @Override
    public GrupoResponse entidadAResponse(Grupo entidad) {
        if (entidad == null) return null;

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
    }*/
}
