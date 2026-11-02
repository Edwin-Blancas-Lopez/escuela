package com.edwin.escuela.entities;

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
    private Long idCurso;

    @Column(name = "NOMBRE", nullable = false, length = 100, unique = true)
    private String nombre;

    @Column(name = "DESCRIPCION", length = 200)
    private String descripcion;

    @Column(name = "CREDITOS", nullable = false)
    private Integer creditos;
}
