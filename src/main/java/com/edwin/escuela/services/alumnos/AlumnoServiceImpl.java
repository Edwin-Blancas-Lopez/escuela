package com.edwin.escuela.services.alumnos;

import com.edwin.escuela.dto.alumnos.AlumnoRequest;
import com.edwin.escuela.dto.alumnos.AlumnoResponse;
import com.edwin.escuela.entities.Alumno;
import com.edwin.escuela.exceptions.EntidadRelacionadaException;
import com.edwin.escuela.mappers.AlumnoMapper;
import com.edwin.escuela.repositories.AlumnoRepository;
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
public class AlumnoServiceImpl implements AlumnoService{

    final AlumnoRepository alumnoRepository;

    final AlumnoMapper alumnoMapper;

    private final InscripcionRepository inscripcionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoResponse> listar() {
        log.info("Listando todos los alumnos...");

        return alumnoRepository.findAll().stream()
                .map(alumnoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoResponse obtenerPorId(Long id) {
        return alumnoMapper.entidadAResponse(obtenerAlumno(id));
    }

    @Override
    public AlumnoResponse registrar(AlumnoRequest request) {
        log.info("Registrando nuevo alumno...");

        Alumno alumno = alumnoMapper.requestAEntidad(
                request,
                generarEmail(request),
                generarMatricula(request));

        alumnoRepository.saveAndFlush(alumno);

        log.info("Nuevo alumno {} registrado", alumno.obtenerNombreCompleto());
        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public AlumnoResponse actualizar(AlumnoRequest request, Long id) {
        Alumno alumno = obtenerAlumno(id);

        log.info("Actualizando alumno con id: {}", id);

        if (alumno.cambioEnDatos(
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno()
        )) {
            log.info("Actualizando alumno con id: {}", id);

            alumno.actualizar(
                    request.nombre(),
                    request.apellidoPaterno(),
                    request.apellidoMaterno(),
                    generarEmail(request),
                    generarMatricula(request));

            alumnoRepository.saveAndFlush(alumno);

            log.info("Datos del alumno {} actualizados", alumno.obtenerNombreCompleto());
        }
        return alumnoMapper.entidadAResponse(alumno);
    }

    @Override
    public void eliminar(Long id) {
        Alumno alumno = obtenerAlumno(id);
        log.info("Eliminando alumno con id: {}", id);

        if (inscripcionRepository.existsByAlumnoId(id))
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el alumno ya que tiene inscripciones asignadas"
            );

        alumnoRepository.delete(alumno);
        alumnoRepository.flush();

        log.info("Alumno {} eliminado", alumno.obtenerNombreCompleto());
    }

    private Alumno obtenerAlumno(Long id){
        return ServiceUtils.obtenerEntidadOException(alumnoRepository, id, Alumno.class);
    }

    private String generarEmail(AlumnoRequest request){
        log.info("Generando email...");

        return alumnoRepository.generarEmail(
                request.nombre().trim(),
                request.apellidoPaterno().trim(),
                request.apellidoMaterno());
    }

    private String generarMatricula(AlumnoRequest request){
        log.info("Generando matricula...");

        return alumnoRepository.generarMatricula(
                request.nombre().trim(),
                request.apellidoPaterno().trim(),
                request.apellidoMaterno());
    }
}
