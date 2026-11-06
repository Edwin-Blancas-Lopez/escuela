package com.edwin.escuela.repositories;

import com.edwin.escuela.entities.Calificacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {
    // En CalificacionRepository:
    boolean existsByInscripcionId(Long inscripcionId);
    boolean existsByInscripcionIdAndIdCalificacionNot(Long inscripcionId, Long id);
}
