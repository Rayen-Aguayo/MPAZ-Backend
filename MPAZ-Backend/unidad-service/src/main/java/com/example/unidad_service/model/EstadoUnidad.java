package com.example.unidad_service.model;

import java.util.List;

public enum EstadoUnidad {
    BORRADOR, EN_REVISION, AUTORIZADA, DESHABILITADA;

    /** A qué estados se puede pasar desde este (nadie se salta pasos). */
    public List<EstadoUnidad> siguientesPermitidos() {
        return switch (this) {
            case BORRADOR -> List.of(EN_REVISION);
            case EN_REVISION -> List.of(AUTORIZADA, BORRADOR);
            case AUTORIZADA -> List.of(DESHABILITADA);
            case DESHABILITADA -> List.of(AUTORIZADA);
        };
    }

    /** Una unidad autorizada no se edita: primero hay que deshabilitarla. */
    public boolean sePuedeEditar() {
        return this != AUTORIZADA;
    }
}
