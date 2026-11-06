package com.edwin.escuela.repositories;

import com.edwin.escuela.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    boolean existsByMaestroId(Long id);
    boolean existsByAulaId(Long id);
    boolean existsByCursoId(Long id);
    // Unicidad para el grupo

    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(Long cursoId, Long maestroId, Long aulaId, String periodo);
    boolean existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(Long cursoId, Long maestroId, Long aulaId, String periodo, Long id);
}
