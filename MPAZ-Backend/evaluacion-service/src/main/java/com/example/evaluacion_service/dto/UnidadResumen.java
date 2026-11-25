package com.example.evaluacion_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UnidadResumen(Long id, String asignatura, String titulo, String codigoOA,
                            String objetivo, String estado) {
}
