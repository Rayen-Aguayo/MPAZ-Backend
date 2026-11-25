package com.example.evaluacion_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PreguntaConEstado(PreguntaDto pregunta, String estadoUnidad) {
}
