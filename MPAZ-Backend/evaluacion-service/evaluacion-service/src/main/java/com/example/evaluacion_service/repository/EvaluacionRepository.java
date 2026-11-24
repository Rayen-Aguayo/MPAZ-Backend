package com.example.evaluacion_service.repository;

import com.example.evaluacion_service.model.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long> {

    Optional<Evaluacion> findByAsignacionId(Long asignacionId);
}
