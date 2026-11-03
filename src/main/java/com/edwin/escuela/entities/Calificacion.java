package com.edwin.escuela.entities;

import com.edwin.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "CALIFICACIONES",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "CALIFICACION_INSCRIPCION_UK",
                        columnNames = "ID_INSCRIPCION"
                )
        }
)
@AllArgsConstructor
@Getter
@Builder
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

    public static void validarDatos(Inscripcion inscripcion, BigDecimal calificacion) {
        if (inscripcion == null) {
            throw new IllegalArgumentException("La inscripción asociada es requerida");
        }
        if (calificacion == null) {
            throw new IllegalArgumentException("La calificación es requerida");
        }
        if (calificacion.compareTo(BigDecimal.ZERO) < 0 || calificacion.compareTo(new BigDecimal("10.0")) > 0) {
            throw new IllegalArgumentException("La calificación debe estar entre 0.0 y 10.0");
        }
    }

    public void actualizar(Inscripcion inscripcion, BigDecimal calificacion) {
        validarDatos(inscripcion, calificacion);

        this.inscripcion = inscripcion;
        this.calificacion = calificacion;
        this.fechaRegistro = LocalDate.now();
    }

    public static Calificacion crear(Inscripcion inscripcion, BigDecimal calificacion) {
        validarDatos(inscripcion, calificacion);



        return Calificacion.builder()
                .inscripcion(inscripcion)
                .calificacion(calificacion)
                .fechaRegistro(LocalDate.now())
                .build();
    }
}