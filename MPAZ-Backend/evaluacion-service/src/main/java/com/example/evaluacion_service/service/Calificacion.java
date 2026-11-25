package com.example.evaluacion_service.service;

/** Escala chilena 1,0 a 7,0 con 60 % de exigencia (60 % correcto = nota 4,0). Igual que la app. */
public final class Calificacion {

    private Calificacion() {
    }

    public static double nota(int aciertos, int total) {
        if (total <= 0) return 1.0;
        double p = (double) aciertos / total;
        double sinRedondear = (p <= 0.60)
            ? 1.0 + (p / 0.60) * 3.0
            : 4.0 + ((p - 0.60) / 0.40) * 3.0;
        return Math.round(sinRedondear * 10) / 10.0;
    }
}
