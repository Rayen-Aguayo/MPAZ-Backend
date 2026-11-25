package com.example.evaluacion_service.dto;

/** Rendimiento agrupado por objetivo de aprendizaje. */
public record ResumenOA(String codigoOA, String objetivo, String asignatura,
                        Long estudiantes, Long intentos, Double promedio) {
}
