package com.example.evaluacion_service.dto;

import java.util.List;

public record MisIntentos(int intentosUsados, int maxIntentos, int intentosRestantes,
                          double mejorNota, List<IntentoDto> intentos) {
}
