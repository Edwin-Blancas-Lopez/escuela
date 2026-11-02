package com.edwin.escuela.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "GRUPOS",
        /*schema = "ESCUELA",*/
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "GRUPO_CU_MA_AU_PE_UK",
                        columnNames = {
                                "ID_CURSO",
                                "ID_MAESTRO",
                                "ID_AULA",
                                "PERIODO"
                        }
                )
        }
)
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_GRUPO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CURSO", nullable = false)
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MAESTRO", nullable = false)
    private Maestro maestro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AULA", nullable = false)
    private Aula aula;

    @Column(name = "PERIODO", nullable = false, length = 20)
    private String periodo;
}