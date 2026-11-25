package com.example.evaluacion_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.evaluacion_service.model.Observacion;

public interface ObservacionRepository extends JpaRepository<Observacion, Long> {

    List<Observacion> findByEstudianteIdOrderByFechaDesc(Long estudianteId);

    List<Observacion> findByEstudianteIdAndUnidadIdOrderByFechaDesc(Long estudianteId, Long unidadId);
}
