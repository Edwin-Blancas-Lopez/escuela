package com.edwin.escuela.services.calificaciones;

import com.edwin.escuela.dto.calificaciones.CalificacionRequest;
import com.edwin.escuela.dto.calificaciones.CalificacionResponse;
import com.edwin.escuela.entities.Calificacion;
import com.edwin.escuela.entities.Inscripcion;
import com.edwin.escuela.exceptions.ConflictoException;
import com.edwin.escuela.mappers.CalificacionMapper;
import com.edwin.escuela.repositories.CalificacionRepository;
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
public class CalificacionServiceImpl implements CalificacionService{
    private final CalificacionRepository calificacionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CalificacionMapper calificacionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CalificacionResponse> listar() {
        log.info("Listando calificaciones...");
        return calificacionRepository.findAll().stream()
                .map(calificacionMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CalificacionResponse obtenerPorId(Long id) {
        return calificacionMapper.entidadAResponse(obtenerCalificacion(id));
    }

    @Override
    public CalificacionResponse registrar(CalificacionRequest request) {
        log.info("Registrando calificación...");
        Inscripcion inscripcion = ServiceUtils.obtenerEntidadOException(inscripcionRepository, request.idInscripcion(), Inscripcion.class);

        if (calificacionRepository.existsByInscripcionId(request.idInscripcion())) {
            throw new ConflictoException("Ya existe una calificación registrada para esta inscripción");
        }

        Calificacion calificacion = Calificacion.crear(inscripcion, request.calificacion());
        calificacionRepository.saveAndFlush(calificacion);

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public CalificacionResponse actualizar(CalificacionRequest request, Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        log.info("Actualizando calificación con id: {}", id);

        Inscripcion inscripcion = ServiceUtils.obtenerEntidadOException(inscripcionRepository, request.idInscripcion(), Inscripcion.class);

        if (calificacionRepository.existsByInscripcionIdAndIdCalificacionNot(request.idInscripcion(), id)) {
            throw new ConflictoException("Ya existe otra calificación asociada a esta inscripción");
        }

        calificacion.actualizar(inscripcion, request.calificacion());
        calificacionRepository.saveAndFlush(calificacion);

        return calificacionMapper.entidadAResponse(calificacion);
    }

    @Override
    public void eliminar(Long id) {
        Calificacion calificacion = obtenerCalificacion(id);
        log.info("Eliminando calificación con id: {}", id);
        calificacionRepository.delete(calificacion);
        calificacionRepository.flush();
    }

    private Calificacion obtenerCalificacion(Long id) {
        return ServiceUtils.obtenerEntidadOException(calificacionRepository, id, Calificacion.class);
    }
}
