package com.edwin.escuela.services.cursos;

import com.edwin.escuela.dto.cursos.CursoRequest;
import com.edwin.escuela.dto.cursos.CursoResponse;
import com.edwin.escuela.entities.Curso;
import com.edwin.escuela.exceptions.ConflictoException;
import com.edwin.escuela.exceptions.EntidadRelacionadaException;
import com.edwin.escuela.mappers.CursoMapper;
import com.edwin.escuela.repositories.CursoRepository;
import com.edwin.escuela.repositories.GrupoRepository;
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
public class CursoServiceImpl implements CursoService {
    final CursoRepository cursoRepository;

    final CursoMapper cursoMapper;

    private final GrupoRepository grupoRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CursoResponse> listar() {
        log.info("Listando todos los cursos...");

        return cursoRepository.findAll().stream()
                .map(cursoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CursoResponse obtenerPorId(Long id) {
        return cursoMapper.entidadAResponse(obtenerCurso(id));
    }


    @Override
    public CursoResponse registrar(CursoRequest request) {
        log.info("Registrando nuevo curso...");

        validarNombreUnico(request.nombre());

        Curso curso = cursoMapper.requestAEntidad(request);
        cursoRepository.saveAndFlush(curso);

        log.info("Nuevo curso {} registrado", curso.getNombre());
        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public CursoResponse actualizar(CursoRequest request, Long id) {
        Curso curso = obtenerCurso(id);

        log.info("Actualizando curso con id: {}", id);

        validarCambioNombreUnico(request.nombre(), id);

        curso.actualizar(
                request.nombre(),
                request.descripcion(),
                request.creditos());

        cursoRepository.saveAndFlush(curso);

        log.info("Datos del curso {} actualizados", curso.getNombre());
        return cursoMapper.entidadAResponse(curso);
    }

    @Override
    public void eliminar(Long id) {
        Curso curso = obtenerCurso(id);
        log.info("Eliminando aula con id: {}", id);

        if (grupoRepository.existsByAulaId(id))
            throw new EntidadRelacionadaException(
                    "No se puede eliminar el curso ya que tiene grupos asignados"
            );

        cursoRepository.delete(curso);
        cursoRepository.flush();

        log.info("Curso {} eliminada", curso.getNombre());
    }

    private Curso obtenerCurso(Long id){
        return ServiceUtils.obtenerEntidadOException(cursoRepository, id, Curso.class);
    }

    private void validarNombreUnico(String nombre) {
        if (cursoRepository.existsByNombreIgnoreCase(nombre.trim())) {
            throw new ConflictoException("Ya existe un curso registrado con el nombre: " + nombre);
        }
    }

    private void validarCambioNombreUnico(String nombre, Long id) {
        if (cursoRepository.existsByNombreIgnoreCaseAndIdNot(nombre.trim(), id)) {
            throw new ConflictoException("Ya existe un curso registrado con el nombre: " + nombre);
        }
    }
}
