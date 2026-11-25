package com.example.unidad_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.unidad_service.model.Pregunta;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByUnidadIdOrderByEsPruebaFinalAscOrdenAsc(Long unidadId);

    List<Pregunta> findByUnidadIdAndEsPruebaFinalOrderByOrdenAsc(Long unidadId, Boolean esPruebaFinal);

    long countByUnidadIdAndEsPruebaFinal(Long unidadId, Boolean esPruebaFinal);

    @Query("select coalesce(max(p.orden), 0) from Pregunta p where p.unidadId = :unidadId and p.esPruebaFinal = :prueba")
    int maxOrden(@Param("unidadId") Long unidadId, @Param("prueba") Boolean prueba);

    void deleteByUnidadId(Long unidadId);
}
