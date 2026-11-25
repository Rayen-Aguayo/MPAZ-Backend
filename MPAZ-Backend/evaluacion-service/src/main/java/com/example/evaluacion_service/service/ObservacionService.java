package com.example.evaluacion_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.evaluacion_service.dto.ObservacionRequest;
import com.example.evaluacion_service.model.Observacion;
import com.example.evaluacion_service.repository.ObservacionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ObservacionService {

    private final ObservacionRepository repo;

    public Observacion crear(ObservacionRequest r, Long docenteId) {
        Observacion o = new Observacion();
        o.setEstudianteId(r.estudianteId());
        o.setUnidadId(r.unidadId());
        o.setDocenteId(docenteId);
        o.setTexto(r.texto().trim());
        o.setFecha(System.currentTimeMillis());
        return repo.save(o);
    }

    public List<Observacion> listar(Long estudianteId, Long unidadId) {
        return unidadId == null
            ? repo.findByEstudianteIdOrderByFechaDesc(estudianteId)
            : repo.findByEstudianteIdAndUnidadIdOrderByFechaDesc(estudianteId, unidadId);
    }
}
