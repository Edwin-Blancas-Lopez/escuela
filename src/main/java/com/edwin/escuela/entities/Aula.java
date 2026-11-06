package com.edwin.escuela.entities;

import com.edwin.escuela.utils.StringCustomUtils;
import com.edwin.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "AULAS"/*schema = "ESCUELA", uniqueConstraints = {@UniqueConstraint(name = "AULA_UK", columnNames = "NOMBRE")}*/)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Aula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AULA")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 30, unique = true)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;

    public static void validarDatos(String nombre, Integer capacidad) {
        StringCustomUtils.validarTamanio(nombre, 1, 30, "El nombre del aula es requerido y debe tener entre 1 y 30 caracteres");

        ValoresNumericosUtils.validarNumeroRequerido(capacidad, "La capacidad es requerida");
        ValoresNumericosUtils.validarNumeroPositivo(capacidad, "La capacidad debe ser mayor a 0");
    }

    public boolean cambioEnDatos(String nombre){
        StringCustomUtils.validarTamanio(nombre, 1, 30, "El nombre del aula es requerido y debe tener entre 1 y 30 caracteres");

        return !this.nombre.equals(nombre);
    }

    public void actualizar(String nombre, Integer capacidad) {
        validarDatos(nombre, capacidad);

        this.nombre = nombre.trim();
        this.capacidad = capacidad;
    }

    public static Aula crear(String nombre, Integer capacidad) {
        validarDatos(nombre, capacidad);
        return Aula.builder()
                .nombre(nombre.trim())
                .capacidad(capacidad)
                .build();
    }
}