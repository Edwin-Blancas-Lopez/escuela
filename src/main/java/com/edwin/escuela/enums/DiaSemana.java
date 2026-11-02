package com.edwin.escuela.enums;

import com.edwin.escuela.exceptions.RecursoNoEncontradoException;
import com.edwin.escuela.utils.StringCustomUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum DiaSemana {

    LUNES("Lunes"),
    MARTES("Martes"),
    MIERCOLES("Miercoles"),
    JUEVES("Jueves"),
    VIERNES("Viernes"),
    SABADO("Sabado");

    private final String descripcion;

    public static DiaSemana obtenerDiaSemanaPorDescripcion(String descripcion){
        StringCustomUtils.validarNoVacio(descripcion, "La descripcion es requerida");

        String descripcionNormalizada = StringCustomUtils.normalizarTexto(descripcion);

        for (DiaSemana diaSemana:values()){
            if (StringCustomUtils.normalizarTexto(diaSemana.descripcion).equalsIgnoreCase(descripcionNormalizada))
                return diaSemana;
        }

        throw new RecursoNoEncontradoException("No existe un dia de la semana con la descripcion: " + descripcion);
    }

}
