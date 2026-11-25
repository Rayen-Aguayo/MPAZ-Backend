package com.example.asignacion_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.asignacion_service.model.Asignacion;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    boolean existsByUnidadIdAndCurso(Long unidadId, String curso);

    Optional<Asignacion> findByUnidadIdAndCurso(Long unidadId, String curso);

    List<Asignacion> findByCurso(String curso);

    List<Asignacion> findByDocenteId(Long docenteId);
}
