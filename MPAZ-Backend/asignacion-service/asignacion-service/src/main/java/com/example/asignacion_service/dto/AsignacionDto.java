package com.example.asignacion_service.dto;

/** "unidad" va null en las consultas internas. "vigente" = hoy está dentro del rango de fechas. */
public record AsignacionDto(
        Long id, Long unidadId, Long docenteId, String curso,
        Long fechaInicio, Long fechaFin, Integer maxIntentos,
        Boolean notificarCurso, Boolean permitirVerRespuestas,
        boolean vigente, UnidadResumen unidad) {
}
