package com.example.evaluacion_service.dto;

/** Seguimiento de un estudiante en una asignación (el nombre se obtiene con GET /auth/estudiantes). */
public record AvanceEstudiante(Long estudianteId, Long intentos, Double mejorNota) {
}
