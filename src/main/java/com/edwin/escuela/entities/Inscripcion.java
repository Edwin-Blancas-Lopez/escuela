package com.edwin.escuela.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "INSCRIPCIONES", /*schema = "ESCUELA",*/ uniqueConstraints = {
        @UniqueConstraint(
                name = "INSCRIPCION_ALU_GRU_UK",
                columnNames = {
                        "ID_ALUMNO",
                        "ID_GRUPO"
                })
        }
)
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INSCRIPCION")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ALUMNO", nullable = false)
    private Alumno alumno;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Builder.Default
    @Column(name = "FECHA_INSCRIPCION")
    private LocalDate fechaInscripcion = LocalDate.now();

    @OneToOne(mappedBy = "inscripcion")
    private Calificacion calificacion;
}