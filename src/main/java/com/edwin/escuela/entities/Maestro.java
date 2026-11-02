package com.edwin.escuela.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "MAESTROS"
       /* schema = "ESCUELA",
        uniqueConstraints = {
                @UniqueConstraint(name = "MAESTRO_EMAIL_UK", columnNames = "EMAIL"),
                @UniqueConstraint(name = "MAESTRO_TELEFONO_UK", columnNames = "TELEFONO")
        }*/
)
@AllArgsConstructor
@Getter
@Builder
@Setter
@NoArgsConstructor
public class Maestro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MAESTRO")
    private Long idMaestro;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "TELEFONO", nullable = false, length = 10, unique = true)
    private String telefono;

    @Builder.Default
    @OneToMany(mappedBy = "maestro", fetch = FetchType.LAZY)
    private List<Grupo> grupos = new ArrayList<>();
}