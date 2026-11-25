package com.example.evaluacion_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.evaluacion_service.dto.AvanceEstudiante;
import com.example.evaluacion_service.dto.PromedioCurso;
import com.example.evaluacion_service.dto.ResumenOA;
import com.example.evaluacion_service.model.IntentoPrueba;

public interface IntentoPruebaRepository extends JpaRepository<IntentoPrueba, Long> {

    long countByAsignacionIdAndEstudianteId(Long asignacionId, Long estudianteId);

    List<IntentoPrueba> findByAsignacionIdAndEstudianteIdOrderByFechaDesc(Long asignacionId, Long estudianteId);

    List<IntentoPrueba> findByEstudianteIdAndUnidadIdOrderByFechaDesc(Long estudianteId, Long unidadId);

    @Query("select new com.example.evaluacion_service.dto.AvanceEstudiante(i.estudianteId, count(i), max(i.nota)) "
         + "from IntentoPrueba i where i.asignacionId = :asignacionId group by i.estudianteId")
    List<AvanceEstudiante> avancePorAsignacion(@Param("asignacionId") Long asignacionId);

    @Query("select new com.example.evaluacion_service.dto.ResumenOA(i.codigoOA, i.objetivo, i.asignatura, "
         + "count(distinct i.estudianteId), count(i), avg(i.nota)) "
         + "from IntentoPrueba i group by i.codigoOA, i.objetivo, i.asignatura order by i.asignatura, i.codigoOA")
    List<ResumenOA> resumenPorOA();

    @Query("select new com.example.evaluacion_service.dto.ResumenOA(i.codigoOA, i.objetivo, i.asignatura, "
         + "count(distinct i.estudianteId), count(i), avg(i.nota)) "
         + "from IntentoPrueba i where i.curso = :curso "
         + "group by i.codigoOA, i.objetivo, i.asignatura order by i.asignatura, i.codigoOA")
    List<ResumenOA> resumenPorOAyCurso(@Param("curso") String curso);

    @Query("select new com.example.evaluacion_service.dto.PromedioCurso(i.curso, count(i), avg(i.nota)) "
         + "from IntentoPrueba i group by i.curso order by i.curso")
    List<PromedioCurso> promedioPorCurso();
}
