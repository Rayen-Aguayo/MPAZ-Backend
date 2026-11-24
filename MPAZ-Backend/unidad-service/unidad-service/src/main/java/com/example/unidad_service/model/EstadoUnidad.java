package com.example.unidad_service.model;

import java.util.List;

/**
 * Estados del ciclo de vida de una unidad didáctica creada por UTP.
 */
public enum EstadoUnidad {
    BORRADOR("Borrador"),
    EN_REVISION("En revisión"),
    AUTORIZADA("Autorizada"),
    DESHABILITADA("Deshabilitada");

    private final String descripcion;

    EstadoUnidad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Transiciones de estado permitidas para evitar saltos en el flujo pedagógico.
     */
    public List<EstadoUnidad> siguientesPermitidos() {
        return switch (this) {
            case BORRADOR -> List.of(EN_REVISION);
            case EN_REVISION -> List.of(AUTORIZADA, BORRADOR);
            case AUTORIZADA -> List.of(DESHABILITADA);
            case DESHABILITADA -> List.of(AUTORIZADA);
        };
    }

    /**
     * Una unidad autorizada no puede modificarse directamente; primero debe deshabilitarse.
     */
    public boolean esEditable() {
        return this != AUTORIZADA;
    }
}
