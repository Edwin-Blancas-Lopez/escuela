    package com.edwin.escuela.services.aulas;

    import com.edwin.escuela.dto.aulas.AulaRequest;
    import com.edwin.escuela.dto.aulas.AulaResponse;
    import com.edwin.escuela.entities.Aula;
    import com.edwin.escuela.exceptions.ConflictoException;
    import com.edwin.escuela.exceptions.EntidadRelacionadaException;
    import com.edwin.escuela.mappers.AulaMapper;
    import com.edwin.escuela.repositories.AulaRepository;
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
    public class AulaServiceImpl implements AulaService{

        final AulaRepository aulaRepository;

        final AulaMapper aulaMapper;

        private final GrupoRepository grupoRepository;

        @Override
        @Transactional(readOnly = true)
        public List<AulaResponse> listar() {
            log.info("Listando todas las aulas...");

            return aulaRepository.findAll().stream()
                    .map(aulaMapper::entidadAResponse)
                    .toList();
        }

        @Override
        @Transactional(readOnly = true)
        public AulaResponse obtenerPorId(Long id) {
            return aulaMapper.entidadAResponse(obtenerAula(id));
        }

        @Override
        public AulaResponse registrar(AulaRequest request) {
            log.info("Registrando nueva aula...");

            validarNombreUnico(request.nombre());

            Aula aula = aulaMapper.requestAEntidad(request);
            aulaRepository.saveAndFlush(aula);

            log.info("Nueva aula {} registrada", aula.getNombre());
            return aulaMapper.entidadAResponse(aula);
        }

        @Override
        public AulaResponse actualizar(AulaRequest request, Long id) {
            Aula aula = obtenerAula(id);

            log.info("Actualizando aula con id: {}", id);

            validarCambioNombreUnico(request.nombre(), id);

            aula.actualizar(
                    request.nombre(),
                    request.capacidad());

            aulaRepository.saveAndFlush(aula);

            log.info("Datos del aula {} actualizados", aula.getNombre());
            return aulaMapper.entidadAResponse(aula);
        }

        @Override
        public void eliminar(Long id) {
            Aula aula = obtenerAula(id);
            log.info("Eliminando aula con id: {}", id);

            if (grupoRepository.existsByAulaId(id))
                throw new EntidadRelacionadaException(
                        "No se puede eliminar el aula ya que tiene grupos asignados"
                );

            aulaRepository.delete(aula);
            aulaRepository.flush();

            log.info("Aula {} eliminada", aula.getNombre());
        }

        private Aula obtenerAula(Long id){
            return ServiceUtils.obtenerEntidadOException(aulaRepository, id, Aula.class);
        }

        private void validarNombreUnico(String nombre) {
            if (aulaRepository.existsByNombreIgnoreCase(nombre.trim())) {
                throw new ConflictoException("Ya existe un aula registrada con el nombre: " + nombre);
            }
        }

        private void validarCambioNombreUnico(String nombre, Long id) {
            if (aulaRepository.existsByNombreIgnoreCaseAndIdNot(nombre.trim(), id)) {
                throw new ConflictoException("Ya existe un aula registrada con el nombre: " + nombre);
            }
        }
    }
