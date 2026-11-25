package com.example.evaluacion_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.evaluacion_service.model.AvanceDesafio;

public interface AvanceDesafioRepository extends JpaRepository<AvanceDesafio, Long> {

    boolean existsByEstudianteIdAndPreguntaId(Long estudianteId, Long preguntaId);

    List<AvanceDesafio> findByEstudianteIdAndUnidadId(Long estudianteId, Long unidadId);
}
