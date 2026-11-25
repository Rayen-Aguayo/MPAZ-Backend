package com.example.unidad_service.dto;

public record UnidadDto(
        Long id, String asignatura, String titulo, String codigoOA, String objetivo,
        String explicacion, String ejemplo, String instrucciones, String imagenUri,
        String estado, Long creadorId, Long creadaEn,
        long totalDesafios, long totalPrueba) {
}
