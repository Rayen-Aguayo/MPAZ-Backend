package com.example.asignacion_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Lo que se necesita de una unidad (viene de unidad-service). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UnidadResumen(Long id, String asignatura, String titulo, String codigoOA,
                            String objetivo, String estado) {
}
