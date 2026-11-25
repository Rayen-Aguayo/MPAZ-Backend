package com.example.unidad_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/** "correcta" va null (y no se envía) cuando la consulta es de un estudiante. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PreguntaDto(
        Long id, Long unidadId, String texto, List<String> alternativas,
        Integer correcta, boolean esPruebaFinal, int orden) {
}
