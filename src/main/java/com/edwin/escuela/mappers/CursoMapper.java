package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.cursos.CursoRequest;
import com.edwin.escuela.dto.cursos.CursoResponse;
import com.edwin.escuela.dto.datos.DatosCurso;
import com.edwin.escuela.entities.Curso;
import org.springframework.stereotype.Component;

@Component
public class CursoMapper implements CommonMapper<CursoRequest, CursoResponse, Curso>{
    @Override
    public Curso requestAEntidad(CursoRequest request) {
        return null;
    }

    @Override
    public CursoResponse entidadAResponse(Curso entidad) {
        return null;
    }

    public DatosCurso entidadADatosCurso(Curso entidad){
        return entidad == null
                ? null
                : new DatosCurso(
                        entidad.getNombre(),
                        entidad.getDescripcion(),
                        entidad.getCreditos());
    }
}
