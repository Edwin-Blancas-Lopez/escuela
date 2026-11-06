package com.edwin.escuela.mappers;

import com.edwin.escuela.dto.aulas.AulaRequest;
import com.edwin.escuela.dto.aulas.AulaResponse;
import com.edwin.escuela.entities.Alumno;
import com.edwin.escuela.entities.Aula;
import org.springframework.stereotype.Component;

@Component
public class AulaMapper implements CommonMapper<AulaRequest, AulaResponse, Aula>{

    @Override
    public Aula requestAEntidad(AulaRequest request) {
        return request == null
                ? null
                : Aula.crear(
                        request.nombre(),
                        request.capacidad()
        );
    }

    @Override
    public AulaResponse entidadAResponse(Aula entidad) {
        return entidad == null
                ? null
                : new AulaResponse(
                        entidad.getId(),
                        entidad.getNombre(),
                        entidad.getCapacidad()
        );
    }
}
