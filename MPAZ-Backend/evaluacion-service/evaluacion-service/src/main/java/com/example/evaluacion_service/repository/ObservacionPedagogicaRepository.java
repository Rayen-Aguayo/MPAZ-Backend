package com.example.evaluacion_service.repository;

import com.example.evaluacion_service.model.ObservacionPedagogica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ObservacionPedagogicaRepository extends JpaRepository<ObservacionPedagogica, Long> {

    List<ObservacionPedagogica> findByEstudianteIdAndUnidadIdOrderByFechaDesc(Long estudianteId, Long unidadId);

    List<ObservacionPedagogica> findByEstudianteIdOrderByFechaDesc(Long estudianteId);
}
