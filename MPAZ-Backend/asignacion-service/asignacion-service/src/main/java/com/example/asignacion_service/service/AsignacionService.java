package com.example.asignacion_service.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.asignacion_service.client.UnidadClient;
import com.example.asignacion_service.dto.AsignacionDto;
import com.example.asignacion_service.dto.AsignacionRequest;
import com.example.asignacion_service.dto.UnidadResumen;
import com.example.asignacion_service.model.Asignacion;
import com.example.asignacion_service.repository.AsignacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AsignacionService {

    private static final long UN_DIA_MS = 24L * 60 * 60 * 1000;

    private final AsignacionRepository repo;
    private final UnidadClient unidades;

    public AsignacionDto crear(AsignacionRequest r, Long docenteId) {
        UnidadResumen unidad = unidades.obtener(r.unidadId());
        if (!"AUTORIZADA".equals(unidad.estado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se pueden asignar unidades autorizadas");
        }
        if (r.fechaFin() < r.fechaInicio()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La fecha de fin no puede ser anterior al inicio");
        }
        String curso = r.curso().trim();
        if (repo.existsByUnidadIdAndCurso(r.unidadId(), curso)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esa unidad ya está asignada a ese curso");
        }
        Asignacion a = new Asignacion();
        a.setUnidadId(r.unidadId());
        a.setDocenteId(docenteId);
        a.setCurso(curso);
        a.setFechaInicio(r.fechaInicio());
        a.setFechaFin(r.fechaFin());
        a.setMaxIntentos(r.maxIntentos());
        a.setNotificarCurso(r.notificarCurso() == null || r.notificarCurso());
        a.setPermitirVerRespuestas(r.permitirVerRespuestas() == null || r.permitirVerRespuestas());
        a.setCreadaEn(System.currentTimeMillis());
        return aDto(repo.save(a), unidad);
    }

    /** Asignaciones que creó el docente (con datos de su unidad). */
    public List<AsignacionDto> delDocente(Long docenteId) {
        return conUnidades(repo.findByDocenteId(docenteId), false);
    }

    /** Asignaciones del curso del estudiante; se omiten las de unidades ya no autorizadas. */
    public List<AsignacionDto> delCurso(String curso) {
        return conUnidades(repo.findByCurso(curso), true);
    }

    public AsignacionDto obtener(Long id) {
        Asignacion a = buscar(id);
        return aDto(a, unidades.obtener(a.getUnidadId()));
    }

    public void eliminar(Long id, Long docenteId) {
        Asignacion a = buscar(id);
        if (!a.getDocenteId().equals(docenteId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo el docente que la creó puede eliminarla");
        }
        repo.delete(a);
    }

    // ---------- Uso interno (evaluacion-service) ----------

    public AsignacionDto interna(Long id) {
        return aDto(buscar(id), null);
    }

    public AsignacionDto buscarPorUnidadYCurso(Long unidadId, String curso) {
        Asignacion a = repo.findByUnidadIdAndCurso(unidadId, curso)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sin asignación para ese curso"));
        return aDto(a, null);
    }

    // ---------- Privados ----------

    private Asignacion buscar(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada"));
    }

    private List<AsignacionDto> conUnidades(List<Asignacion> lista, boolean soloAutorizadas) {
        Map<Long, UnidadResumen> mapa = unidades.porIds(lista.stream().map(Asignacion::getUnidadId).toList());
        return lista.stream()
            .filter(a -> mapa.containsKey(a.getUnidadId()))
            .filter(a -> !soloAutorizadas || "AUTORIZADA".equals(mapa.get(a.getUnidadId()).estado()))
            .map(a -> aDto(a, mapa.get(a.getUnidadId())))
            .toList();
    }

    private AsignacionDto aDto(Asignacion a, UnidadResumen unidad) {
        long ahora = System.currentTimeMillis();
        // fechaFin es medianoche UTC del día elegido: ese día completo sigue vigente
        boolean vigente = ahora >= a.getFechaInicio() && ahora < a.getFechaFin() + UN_DIA_MS;
        return new AsignacionDto(a.getId(), a.getUnidadId(), a.getDocenteId(), a.getCurso(),
            a.getFechaInicio(), a.getFechaFin(), a.getMaxIntentos(),
            a.getNotificarCurso(), a.getPermitirVerRespuestas(), vigente, unidad);
    }
}
