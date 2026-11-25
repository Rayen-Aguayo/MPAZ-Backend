package com.example.evaluacion_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AsignacionDto(Long id, Long unidadId, Long docenteId, String curso,
                            Long fechaInicio, Long fechaFin, Integer maxIntentos,
                            Boolean notificarCurso, Boolean permitirVerRespuestas, boolean vigente) {
}
