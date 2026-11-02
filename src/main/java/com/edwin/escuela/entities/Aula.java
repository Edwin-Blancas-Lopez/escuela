package com.edwin.escuela.entities;

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
    private Long idAula;

    @Column(name = "NOMBRE", nullable = false, length = 30, unique = true)
    private String nombre;

    @Column(name = "CAPACIDAD", nullable = false)
    private Integer capacidad;
}