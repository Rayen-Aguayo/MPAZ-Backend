package com.example.evaluacion_service.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PreguntaDto(Long id, Long unidadId, String texto, List<String> alternativas,
                          Integer correcta, boolean esPruebaFinal, int orden) {
}
