package com.edwin.escuela.services.inscripciones;

import com.edwin.escuela.dto.inscripciones.InscripcionRequest;
import com.edwin.escuela.dto.inscripciones.InscripcionResponse;
import com.edwin.escuela.entities.Alumno;
import com.edwin.escuela.entities.Grupo;
import com.edwin.escuela.entities.Inscripcion;
import com.edwin.escuela.exceptions.ConflictoException;
import com.edwin.escuela.exceptions.EntidadRelacionadaException;
import com.edwin.escuela.mappers.InscripcionMapper;
import com.edwin.escuela.repositories.AlumnoRepository;
import com.edwin.escuela.repositories.CalificacionRepository;
import com.edwin.escuela.repositories.GrupoRepository;
import com.edwin.escuela.repositories.InscripcionRepository;
import com.edwin.escuela.utils.ServiceUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class InscripcionServiceImpl implements InscripcionService{
    private final InscripcionRepository inscripcionRepository;
    private final AlumnoRepository alumnoRepository;
    private final GrupoRepository grupoRepository;
    private final CalificacionRepository calificacionRepository;
    private final InscripcionMapper inscripcionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<InscripcionResponse> listar() {
        log.info("Listando inscripciones...");
        return inscripcionRepository.findAll().stream()
                .map(inscripcionMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public InscripcionResponse obtenerPorId(Long id) {
        return inscripcionMapper.entidadAResponse(obtenerInscripcion(id));
    }

    @Override
    public InscripcionResponse registrar(InscripcionRequest request) {
        log.info("Registrando inscripción...");
        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        if (inscripcionRepository.existsByAlumnoIdAndGrupoId(request.idAlumno(), request.idGrupo())) {
            throw new ConflictoException("El alumno ya se encuentra inscrito en este grupo");
        }

        Inscripcion inscripcion = Inscripcion.crear(alumno, grupo);
        inscripcionRepository.saveAndFlush(inscripcion);

        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public InscripcionResponse actualizar(InscripcionRequest request, Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        log.info("Actualizando inscripción con id: {}", id);

        Alumno alumno = obtenerAlumno(request.idAlumno());
        Grupo grupo = obtenerGrupo(request.idGrupo());

        if (inscripcionRepository.existsByAlumnoIdAndGrupoIdAndIdNot(request.idAlumno(), request.idGrupo(), id)) {
            throw new ConflictoException("Ya existe otra inscripción activa con este alumno y grupo");
        }

        inscripcion.actualizar(alumno, grupo);
        inscripcionRepository.saveAndFlush(inscripcion);

        return inscripcionMapper.entidadAResponse(inscripcion);
    }

    @Override
    public void eliminar(Long id) {
        Inscripcion inscripcion = obtenerInscripcion(id);
        log.info("Eliminando inscripción con id: {}", id);

        if (calificacionRepository.existsByInscripcionId(id)) {
            throw new EntidadRelacionadaException("No se puede eliminar la inscripción ya que tiene una calificación asociada");
        }

        inscripcionRepository.delete(inscripcion);
        inscripcionRepository.flush();
    }

    private Inscripcion obtenerInscripcion(Long id) {
        return ServiceUtils.obtenerEntidadOException(inscripcionRepository, id, Inscripcion.class);
    }

    private Alumno obtenerAlumno(Long id) {
        return ServiceUtils.obtenerEntidadOException(alumnoRepository, id, Alumno.class);
    }

    private Grupo obtenerGrupo(Long id) {
        return ServiceUtils.obtenerEntidadOException(grupoRepository, id, Grupo.class);
    }
}
