package com.edwin.escuela.repositories;

import com.edwin.escuela.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HorarioRepository extends JpaRepository<Horario, Long> {
    // 1. Buscar los horarios asociados a un grupo específico
    List<Horario> findByGrupoId(Long grupoId);

    // 2. Buscar los horarios de una misma aula filtrados por el periodo del grupo
    @Query("SELECT h FROM Horario h JOIN h.grupo g WHERE g.aula.id = :aulaId AND g.periodo = :periodo")
    List<Horario> findByAulaIdAndPeriodo(@Param("aulaId") Long aulaId, @Param("periodo") String periodo);
}
