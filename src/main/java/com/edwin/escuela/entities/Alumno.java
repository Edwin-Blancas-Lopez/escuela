package com.edwin.escuela.entities;

import com.edwin.escuela.utils.StringCustomUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "ALUMNOS")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Alumno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ALUMNO")
    private Long id;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDO_PATERNO", nullable = false, length = 50)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false, length = 50)
    private String apellidoMaterno;

    @Column(name = "EMAIL", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "MATRICULA", nullable = false, length = 10, unique = true)
    private String matricula;

    @Builder.Default
    @Column(name = "FECHA_INGRESO")
    private LocalDate fechaIngreso = LocalDate.now();

    @Builder.Default
    @OneToMany(mappedBy = "alumno", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public String obtenerNombreCompleto(){
        return String.join(" ",
                this.getNombre(),
                this.getApellidoPaterno(),
                this.getApellidoMaterno());
    }

    public BigDecimal calcularPromedio() {
        // 2. Extraer únicamente los valores numéricos de las calificaciones que no sean nulos
        List<BigDecimal> calificaciones = inscripciones.stream()
                .map(Inscripcion::getCalificacion) // Obtenemos el objeto Calificacion (puede ser null)
                .filter(Objects::nonNull) // Filtramos si la inscripción no tiene calificación registrada
                .map(Calificacion::getCalificacion) // Extraemos el BigDecimal de la calificación
                .filter(Objects::nonNull).toList();

        // 3. Si no tiene ninguna calificación registrada aún, retornamos 0
        if (calificaciones.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 4. Sumar todas las calificaciones usando reduce
        BigDecimal suma = calificaciones.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 5. Dividir la suma entre el total de calificaciones válidas
        // Usamos scale 1 y RoundingMode.HALF_UP para mantener el formato de 1 decimal (ej. 9.5)
        return suma.divide(BigDecimal.valueOf(calificaciones.size()), 2, RoundingMode.HALF_UP);
    }

    public static void validarDatos(String nombre, String apellidoPaterno, String apellidoMaterno) {
        StringCustomUtils.validarTamanio(nombre, 1, 50, "El nombre es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoPaterno, 1, 50, "El apellido paterno es requerido y debe tener entre 1 y 50 caracteres");
        StringCustomUtils.validarTamanio(apellidoMaterno, 1, 50, "El apellido materno es requerido y debe tener entre 1 y 50 caracteres");
    }

    public void asignarDatosAcademicos(String email, String matricula){
        StringCustomUtils.validarTamanio(email, 8, 100, "El email es requerido y debe tener entre 8 y 100 caracteres");
        StringCustomUtils.validarTamanio(matricula, 1, 10, "La matrícula es requerida y debe tener entre 1 y 10 caracteres");
        this.email = email.trim().toLowerCase().trim();
        this.matricula = matricula.trim();
    }

    public boolean cambioEnDatos(String nombre, String apellidoPaterno, String apellidoMaterno){
        validarDatos(nombre, apellidoPaterno, apellidoMaterno);

        return !this.nombre.equals(nombre) ||
                !this.apellidoPaterno.equals(apellidoPaterno) ||
                !this.apellidoMaterno.equals(apellidoMaterno);
    }

    public void actualizar(String nombre, String apellidoPaterno, String apellidoMaterno, String email, String matricula) {
        validarDatos(nombre, apellidoPaterno, apellidoMaterno);

        asignarDatosAcademicos(email, matricula);

        this.nombre = nombre.trim();
        this.apellidoPaterno = apellidoPaterno.trim();
        this.apellidoMaterno = apellidoMaterno.trim();
    }

    public static Alumno crear(String nombre, String apellidoPaterno, String apellidoMaterno) {
        validarDatos(nombre, apellidoPaterno, apellidoMaterno);

        return Alumno.builder()
                .nombre(nombre.trim())
                .apellidoPaterno(apellidoPaterno.trim())
                .apellidoMaterno(apellidoMaterno.trim())
                .build();
    }
}
