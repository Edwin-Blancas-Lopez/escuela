package com.edwin.escuela.services.horarios;

import com.edwin.escuela.dto.horarios.HorarioRequest;
import com.edwin.escuela.dto.horarios.HorarioResponse;
import com.edwin.escuela.entities.Grupo;
import com.edwin.escuela.entities.Horario;
import com.edwin.escuela.exceptions.ConflictoException;
import com.edwin.escuela.mappers.HorarioMapper;
import com.edwin.escuela.repositories.GrupoRepository;
import com.edwin.escuela.repositories.HorarioRepository;
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
public class HorarioServiceImpl implements HorarioService{
    private final HorarioRepository horarioRepository;
    private final GrupoRepository grupoRepository;
    private final HorarioMapper horarioMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HorarioResponse> listar() {
        log.info("Listando todos los horarios...");
        return horarioRepository.findAll().stream()
                .map(horarioMapper::entidadAResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HorarioResponse obtenerPorId(Long id) {
        return horarioMapper.entidadAResponse(obtenerHorario(id));
    }

    @Override
    public HorarioResponse registrar(HorarioRequest request) {
        log.info("Registrando nuevo horario...");
        Grupo grupo = ServiceUtils.obtenerEntidadOException(grupoRepository, request.idGrupo(), Grupo.class);

        validarTraslapes(grupo, request.dia(), request.horaInicio(), request.horaFin(), null);

        Horario horario = Horario.crear(grupo, request.dia(), request.horaInicio(), request.horaFin());
        horarioRepository.saveAndFlush(horario);

        log.info("Horario registrado con éxito para el grupo {}", grupo.getId());
        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public HorarioResponse actualizar(HorarioRequest request, Long id) {
        Horario horario = obtenerHorario(id);
        log.info("Actualizando horario con id: {}", id);

        Grupo grupo = ServiceUtils.obtenerEntidadOException(grupoRepository, request.idGrupo(), Grupo.class);

        validarTraslapes(grupo, request.dia(), request.horaInicio(), request.horaFin(), id);

        horario.actualizar(grupo, request.dia(), request.horaInicio(), request.horaFin());
        horarioRepository.saveAndFlush(horario);

        log.info("Horario con id {} actualizado", id);
        return horarioMapper.entidadAResponse(horario);
    }

    @Override
    public void eliminar(Long id) {
        Horario horario = obtenerHorario(id);
        log.info("Eliminando horario con id: {}", id);
        horarioRepository.delete(horario);
        horarioRepository.flush();
    }

    private Horario obtenerHorario(Long id) {
        return ServiceUtils.obtenerEntidadOException(horarioRepository, id, Horario.class);
    }

    private void validarTraslapes(Grupo grupo, com.edwin.escuela.enums.DiaSemana dia, String inicio, String fin, Long idExcluir) {
        // 1. Traslape por grupo en el mismo día
        List<Horario> horariosGrupo = horarioRepository.findByGrupoId(grupo.getId());
        for (Horario h : horariosGrupo) {
            if (idExcluir != null && h.getId().equals(idExcluir)) continue;
            if (h.getDiaSemana() == dia && hayTraslape(inicio, fin, h.getHoraInicio(), h.getHoraFin())) {
                throw new ConflictoException("El horario se traslapa con otra clase del mismo grupo");
            }
        }

        // 2. Traslape por aula en el mismo periodo y mismo día
        List<Horario> horariosAula = horarioRepository.findByAulaIdAndPeriodo(grupo.getAula().getId(), grupo.getPeriodo());
        for (Horario h : horariosAula) {
            if (idExcluir != null && h.getId().equals(idExcluir)) continue;
            if (h.getDiaSemana() == dia && hayTraslape(inicio, fin, h.getHoraInicio(), h.getHoraFin())) {
                throw new ConflictoException("El horario se traslapa con otra clase asignada al aula en este periodo");
            }
        }
    }

    private boolean hayTraslape(String ini1, String fin1, String ini2, String fin2) {
        return ini1.compareTo(fin2) < 0 && fin1.compareTo(ini2) > 0;
    }
}
