package com.example.evaluacion_service.repository;

import com.example.evaluacion_service.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntentoEvaluacionRepository extends JpaRepository<IntentoEvaluacion, Long> {

    long countByEvaluacionIdAndEstudianteId(Long evaluacionId, Long estudianteId);

    List<IntentoEvaluacion> findByEvaluacionIdAndEstudianteIdOrderByFechaInicioDesc(Long evaluacionId, Long estudianteId);

    List<IntentoEvaluacion> findByEstudianteIdOrderByFechaInicioDesc(Long estudianteId);
}