package com.edwin.escuela.entities;

import com.edwin.escuela.enums.DiaSemana;
import com.edwin.escuela.utils.StringCustomUtils;
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

    public static void validarDatos(Grupo grupo, DiaSemana diaSemana, String horaInicio, String horaFin) {
        if (grupo == null) {
            throw new IllegalArgumentException("El grupo es requerido");
        }
        if (diaSemana == null) {
            throw new IllegalArgumentException("El día de la semana es requerido");
        }
        StringCustomUtils.validarTamanio(horaInicio, 5, 5, "La hora de inicio es requerida y debe tener el formato HH:MM");
        StringCustomUtils.validarTamanio(horaFin, 5, 5, "La hora de fin es requerida y debe tener el formato HH:MM");
    }

    public void actualizar(Grupo grupo, DiaSemana diaSemana, String horaInicio, String horaFin) {
        validarDatos(grupo, diaSemana, horaInicio, horaFin);

        this.grupo = grupo;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio.trim();
        this.horaFin = horaFin.trim();
    }

    public static Horario crear(Grupo grupo, DiaSemana diaSemana, String horaInicio, String horaFin) {
        validarDatos(grupo, diaSemana, horaInicio, horaFin);

        return Horario.builder()
                .grupo(grupo)
                .diaSemana(diaSemana)
                .horaInicio(horaInicio.trim())
                .horaFin(horaFin.trim())
                .build();
    }
}