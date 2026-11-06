package com.edwin.escuela.entities;

import com.edwin.escuela.utils.StringCustomUtils;
import com.edwin.escuela.utils.ValoresNumericosUtils;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "CURSOS"
        /*schema = "ESCUELA",
        uniqueConstraints = {
                @UniqueConstraint(name = "CURSO_UK", columnNames = "NOMBRE")
        }*/
)
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CURSO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "CREDITOS", nullable = false)
    private Integer creditos;

    public static void validarDatos(String nombre, String descripcion, Integer creditos) {
        StringCustomUtils.validarTamanio(nombre, 1, 100, "El nombre del curso es requerido y debe tener entre 1 y 100 caracteres");
        StringCustomUtils.validarTamanio(descripcion, 1, 200, "La descripcion del curso es requerids y debe tener entre 1 y 200 caracteres");
        ValoresNumericosUtils.validarNumeroRequerido(creditos, "Los creditos son requeridos ");
        ValoresNumericosUtils.validarNumeroPositivo(creditos, "Los creditos deben ser mayores a 0");
    }

    public boolean cambioEnDatos(String nombre, String descripcion, Integer creditos){
        validarDatos(nombre, descripcion, creditos);
        return !this.nombre.equals(nombre) || !this.descripcion.equals(descripcion) || !this.creditos.equals(creditos);
    }

    public void actualizar(String nombre, String descripcion, Integer creditos) {
        validarDatos(nombre, descripcion, creditos);

        this.nombre = nombre.trim();
        this.descripcion = descripcion.trim();
        this.creditos = creditos;
    }

    public static Curso crear(String nombre, String descripcion, Integer creditos) {
        validarDatos(nombre, descripcion, creditos);

        return Curso.builder()
                .nombre(nombre.trim())
                .descripcion(descripcion.trim())
                .creditos(creditos)
                .build();
    }
}
