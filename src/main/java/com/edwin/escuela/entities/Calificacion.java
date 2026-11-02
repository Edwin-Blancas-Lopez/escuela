package com.edwin.escuela.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "CALIFICACIONES",
        schema = "ESCUELA",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "CALIFICACION_INSCRIPCION_UK",
                        columnNames = "ID_INSCRIPCION"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Calificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CALIFICACION")
    private Long idCalificacion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ID_INSCRIPCION", nullable = false, unique = true)
    private Inscripcion inscripcion;

    @Column(name = "CALIFICACION", nullable = false, precision = 3, scale = 1)
    private BigDecimal calificacion;

    @Column(name = "FECHA_REGISTRO")
    private LocalDate fechaRegistro;
}