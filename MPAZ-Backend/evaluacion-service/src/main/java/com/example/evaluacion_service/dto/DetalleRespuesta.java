package com.example.evaluacion_service.dto;

public record DetalleRespuesta(Long preguntaId, Integer elegida, boolean correcta, Integer respuestaCorrecta) {
}
