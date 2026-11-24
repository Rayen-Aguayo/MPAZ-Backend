package com.example.evaluacion_service.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/** Conversión de MPAZ PRO: escala 1,0–7,0 y exigencia configurable (por defecto 60%). */
@Service
public class CalificacionService {

    public BigDecimal calcularNota(int aciertos, int total, BigDecimal exigencia) {
        if (total <= 0) {
            return new BigDecimal("1.0");
        }
        double p = (double) aciertos / total;
        double corte = exigencia.doubleValue() / 100.0;
        double nota = p <= corte
                ? 1.0 + (p / corte) * 3.0
                : 4.0 + ((p - corte) / (1.0 - corte)) * 3.0;
        return BigDecimal.valueOf(nota).setScale(1, RoundingMode.HALF_UP);
    }
}
