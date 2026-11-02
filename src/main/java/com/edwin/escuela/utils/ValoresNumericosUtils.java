package com.edwin.escuela.utils;


import com.edwin.escuela.exceptions.DatoInvalidoException;

import java.math.BigDecimal;

public class ValoresNumericosUtils {

    public static <N extends Number> void validarNumeroRequerido(N numero, String mensaje){
        if (numero == null)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarNumeroPositivo(Integer numero, String mensaje){
        validarNumeroRequerido(numero, mensaje);

        if (numero <= 0)
            throw new DatoInvalidoException(mensaje);
    }

    public static void validarBigDecilmalPositivo(BigDecimal numero, String mensaje){

        validarNumeroRequerido(numero, mensaje);

        if (numero.compareTo(BigDecimal.ZERO) <= 0)
            throw new DatoInvalidoException(mensaje);
    }
}
