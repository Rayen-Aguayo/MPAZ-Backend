package com.example.evaluacion_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/** "detalle" solo se envía si la asignación permite ver respuestas. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record IntentoDto(Long id, Long asignacionId, Long estudianteId, int aciertos, int total,
                         double nota, Long fecha, Integer intentosUsados, Integer intentosRestantes,
                         List<DetalleRespuesta> detalle) {
}
