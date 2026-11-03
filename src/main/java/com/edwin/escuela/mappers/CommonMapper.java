package com.edwin.escuela.mappers;

public interface CommonMapper<RQ, RS, E> {
    E requestAEntidad(RQ request);
    RS entidadAResponse(E entidad);
}
