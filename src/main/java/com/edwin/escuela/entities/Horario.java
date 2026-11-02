package com.edwin.escuela.entities;

import com.edwin.escuela.enums.DiaSemana;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "HORARIOS" /*schema = "ESCUELA"*/)
@AllArgsConstructor
@Builder
@Getter
@Setter
@NoArgsConstructor
public class Horario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_HORARIO")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPO", nullable = false)
    private Grupo grupo;

    @Enumerated(EnumType.STRING)
    @Column(name = "DIA", nullable = false)
    private DiaSemana diaSemana;

    @Column(name = "HORA_INICIO", nullable = false, length = 5)
    private String horaInicio;

    @Column(name = "HORA_FIN", nullable = false, length = 5)
    private String horaFin;
}