package com.edwin.escuela.repositories;

import com.edwin.escuela.entities.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {
    boolean existsByMaestroId(Long id);

}
