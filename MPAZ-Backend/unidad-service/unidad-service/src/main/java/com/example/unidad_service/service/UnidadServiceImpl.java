package com.example.unidad_service.service;

import com.example.unidad_service.DTO.CambioEstadoRequestDTO;
import com.example.unidad_service.DTO.UnidadRequestDTO;
import com.example.unidad_service.DTO.UnidadResponseDTO;
import com.example.unidad_service.client.AsignacionClient;
import com.example.unidad_service.client.UsuarioClient;
import com.example.unidad_service.model.EstadoUnidad;
import com.example.unidad_service.model.Unidad;
import com.example.unidad_service.repository.UnidadRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UnidadServiceImpl implements UnidadService {

    private static final Set<String> ASIGNATURAS_VALIDAS =
            Set.of(
                    "Lenguaje y Comunicación",
                    "Matemática",
                    "Ciencias Naturales",
                    "Historia, Geografía y Cs. Sociales");

    private final UnidadRepository unidadRepository;
    private final UsuarioClient usuarioClient;
    private final AsignacionClient asignacionClient;

    public UnidadServiceImpl(
            UnidadRepository unidadRepository,
            UsuarioClient usuarioClient,
            AsignacionClient asignacionClient) {
        this.unidadRepository = unidadRepository;
        this.usuarioClient = usuarioClient;
        this.asignacionClient = asignacionClient;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadResponseDTO> listarUnidades(EstadoUnidad estado, String asignatura) {
        boolean tieneAsignatura = asignatura != null && !asignatura.isBlank();
        List<Unidad> unidades;

        if (estado != null && tieneAsignatura) {
            unidades =
                    unidadRepository.findByEstadoAndAsignaturaIgnoreCaseOrderByCodigoOAAsc(
                            estado, asignatura.trim());
        } else if (estado != null) {
            unidades = unidadRepository.findByEstadoOrderByAsignaturaAscCodigoOAAsc(estado);
        } else if (tieneAsignatura) {
            unidades =
                    unidadRepository.findByAsignaturaIgnoreCaseOrderByCodigoOAAsc(
                            asignatura.trim());
        } else {
            unidades = unidadRepository.findAllByOrderByCreadaEnDesc();
        }

        return unidades.stream().map(UnidadResponseDTO::fromEntity).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UnidadResponseDTO> listarAutorizadas() {
        return unidadRepository
                .findByEstadoOrderByAsignaturaAscCodigoOAAsc(EstadoUnidad.AUTORIZADA)
                .stream()
                .map(UnidadResponseDTO::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UnidadResponseDTO obtenerPorId(Long id) {
        Unidad unidad = buscarUnidadEntity(id);
        return UnidadResponseDTO.fromEntity(unidad);
    }

    @Override
    public UnidadResponseDTO crearUnidad(UnidadRequestDTO request) {
        validarAsignatura(request.asignatura());
        if (!usuarioClient.esUsuarioUtpValido(request.creadorId())) {
            throw new IllegalArgumentException(
                    "El creador indicado no existe o no tiene perfil de Jefatura UTP.");
        }

        Unidad unidad = new Unidad();
        aplicarDatosUnidad(unidad, request);
        unidad.setEstado(EstadoUnidad.BORRADOR);
        unidad.setCreadorId(request.creadorId());

        Unidad guardada = unidadRepository.save(unidad);
        return UnidadResponseDTO.fromEntity(guardada);
    }

    @Override
    public UnidadResponseDTO actualizarUnidad(Long id, UnidadRequestDTO request) {
        Unidad unidad = buscarUnidadEntity(id);
        if (!unidad.getEstado().esEditable()) {
            throw new IllegalStateException(
                    "Una unidad autorizada no se puede editar. Deshabilítala primero.");
        }
        validarAsignatura(request.asignatura());
        aplicarDatosUnidad(unidad, request);

        Unidad actualizada = unidadRepository.save(unidad);
        return UnidadResponseDTO.fromEntity(actualizada);
    }

    @Override
    public UnidadResponseDTO cambiarEstado(Long id, CambioEstadoRequestDTO request) {
        Unidad unidad = buscarUnidadEntity(id);
        EstadoUnidad actual = unidad.getEstado();
        EstadoUnidad destino = request.estado();

        if (!actual.siguientesPermitidos().contains(destino)) {
            throw new IllegalStateException(
                    String.format(
                            "No se puede pasar de %s a %s.",
                            actual.getDescripcion(), destino.getDescripcion()));
        }

        unidad.setEstado(destino);
        Unidad actualizada = unidadRepository.save(unidad);

        if (destino == EstadoUnidad.DESHABILITADA) {
            asignacionClient.notificarUnidadDeshabilitada(id);
        }

        return UnidadResponseDTO.fromEntity(actualizada);
    }

    @Override
    public void eliminarUnidad(Long id) {
        Unidad unidad = buscarUnidadEntity(id);
        if (!unidad.getEstado().esEditable()) {
            throw new IllegalStateException(
                    "No se puede eliminar una unidad autorizada. Deshabilítala primero.");
        }
        unidadRepository.delete(unidad);
    }

    private Unidad buscarUnidadEntity(Long id) {
        return unidadRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidad no encontrada con id: " + id));
    }

    private void aplicarDatosUnidad(Unidad unidad, UnidadRequestDTO request) {
        unidad.setAsignatura(request.asignatura().trim());
        unidad.setTitulo(request.titulo().trim());
        unidad.setCodigoOA(normalizarOA(request.codigoOA()));
        unidad.setObjetivo(request.objetivo().trim());
        unidad.setExplicacion(request.explicacion().trim());
        unidad.setEjemplo(request.ejemplo().trim());
        unidad.setInstrucciones(request.instrucciones().trim());
        unidad.setImagenUri(
                request.imagenUri() != null && !request.imagenUri().isBlank()
                        ? request.imagenUri().trim()
                        : null);
    }

    private void validarAsignatura(String asignatura) {
        boolean valida =
                ASIGNATURAS_VALIDAS.stream()
                        .anyMatch(a -> a.equalsIgnoreCase(asignatura.trim()));
        if (!valida) {
            throw new IllegalArgumentException(
                    "Asignatura inválida. Valores permitidos: " + ASIGNATURAS_VALIDAS);
        }
    }

    /** Normaliza entradas como "oa3" o "OA3" al formato estándar "OA 3". */
    private String normalizarOA(String codigoOA) {
        String limpio = codigoOA.trim().toUpperCase();
        if (limpio.startsWith("OA")) {
            limpio = limpio.substring(2).trim();
        }
        return "OA " + limpio;
    }
}
