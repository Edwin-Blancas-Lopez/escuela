package com.edwin.escuela.services.grupos;

import com.edwin.escuela.dto.grupos.GrupoRequest;
import com.edwin.escuela.dto.grupos.GrupoResponse;
import com.edwin.escuela.entities.Aula;
import com.edwin.escuela.entities.Curso;
import com.edwin.escuela.entities.Grupo;
import com.edwin.escuela.entities.Maestro;
import com.edwin.escuela.exceptions.ConflictoException;
import com.edwin.escuela.exceptions.EntidadRelacionadaException;
import com.edwin.escuela.mappers.GrupoMapper;
import com.edwin.escuela.repositories.*;
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
public class GrupoServiceImpl implements GrupoService{

    private final GrupoRepository grupoRepository;
    private final CursoRepository cursoRepository;
    private final MaestroRepository maestroRepository;
    private final AulaRepository aulaRepository;
    private final InscripcionRepository inscripcionRepository;
    private final HorarioRepository horarioRepository;
    private final GrupoMapper grupoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<GrupoResponse> listar() {
        log.info("Listando todos los grupos...");
        return grupoRepository.findAll().stream()
                .map(grupoMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public GrupoResponse obtenerPorId(Long id) {
        return grupoMapper.entidadAResponse(obtenerGrupo(id));
    }

    @Override
    public GrupoResponse registrar(GrupoRequest request) {
        log.info("Registrando nuevo grupo...");

        Curso curso = obtenerCurso(request.idCurso());
        Maestro maestro = obtenerMaestro(request.idMaestro());
        Aula aula = obtenerAula(request.idAula());

        validarUnicidad(request.idCurso(), request.idMaestro(), request.idAula(), request.periodo(), null);

        Grupo grupo = Grupo.crear(curso, maestro, aula, request.periodo());
        grupoRepository.saveAndFlush(grupo);

        log.info("Grupo con id {} registrado exitosamente", grupo.getId());
        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    public GrupoResponse actualizar(GrupoRequest request, Long id) {
        Grupo grupo = obtenerGrupo(id);
        log.info("Actualizando grupo con id: {}", id);

        Curso curso = obtenerCurso(request.idCurso());
        Maestro maestro = obtenerMaestro(request.idMaestro());
        Aula aula = obtenerAula(request.idAula());

        validarUnicidad(request.idCurso(), request.idMaestro(), request.idAula(), request.periodo(), id);

        grupo.actualizar(curso, maestro, aula, request.periodo());
        grupoRepository.saveAndFlush(grupo);

        log.info("Grupo con id {} actualizado", id);
        return grupoMapper.entidadAResponse(grupo);
    }

    @Override
    public void eliminar(Long id) {
        Grupo grupo = obtenerGrupo(id);
        log.info("Eliminando grupo con id: {}", id);

        boolean tieneInscripciones = inscripcionRepository.existsByGrupoId(id);
        boolean tieneHorarios = !horarioRepository.findByGrupoId(id).isEmpty();

        if (tieneInscripciones) {
            throw new EntidadRelacionadaException("No se puede eliminar el grupo ya que tiene inscripciones asociadas");
        }

        if (tieneHorarios) {
            throw new EntidadRelacionadaException("No se puede eliminar el grupo ya que tiene horarios asociados");
        }

        grupoRepository.delete(grupo);
        grupoRepository.flush();
        log.info("Grupo con id {} eliminado", id);
    }

    private Grupo obtenerGrupo(Long id) {
        return ServiceUtils.obtenerEntidadOException(grupoRepository, id, Grupo.class);
    }

    private Curso obtenerCurso(Long id) {
        return ServiceUtils.obtenerEntidadOException(cursoRepository, id, Curso.class);
    }

    private Maestro obtenerMaestro(Long id) {
        return ServiceUtils.obtenerEntidadOException(maestroRepository, id, Maestro.class);
    }

    private Aula obtenerAula(Long id) {
        return ServiceUtils.obtenerEntidadOException(aulaRepository, id, Aula.class);
    }

    private void validarUnicidad(Long cursoId, Long maestroId, Long aulaId, String periodo, Long idExcluir) {
        boolean existe = (idExcluir == null)
                ? grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodo(cursoId, maestroId, aulaId, periodo)
                : grupoRepository.existsByCursoIdAndMaestroIdAndAulaIdAndPeriodoAndIdNot(cursoId, maestroId, aulaId, periodo, idExcluir);

        if (existe) {
            throw new ConflictoException("Ya existe un grupo con la misma combinación de curso, maestro, aula y periodo");
        }
    }
}
