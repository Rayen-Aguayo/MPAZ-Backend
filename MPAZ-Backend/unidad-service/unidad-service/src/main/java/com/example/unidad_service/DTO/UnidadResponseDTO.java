package com.example.unidad_service.DTO;

import com.example.unidad_service.model.EstadoUnidad;
import com.example.unidad_service.model.Unidad;
import java.time.LocalDateTime;

/** DTO de salida con la información completa de una unidad didáctica. */
public record UnidadResponseDTO(
        Long id,
        String asignatura,
        String titulo,
        String codigoOA,
        String objetivo,
        String explicacion,
        String ejemplo,
        String instrucciones,
        String imagenUri,
        EstadoUnidad estado,
        String estadoDescripcion,
        Long creadorId,
        LocalDateTime creadaEn) {

    public static UnidadResponseDTO fromEntity(Unidad unidad) {
        return new UnidadResponseDTO(
                unidad.getId(),
                unidad.getAsignatura(),
                unidad.getTitulo(),
                unidad.getCodigoOA(),
                unidad.getObjetivo(),
                unidad.getExplicacion(),
                unidad.getEjemplo(),
                unidad.getInstrucciones(),
                unidad.getImagenUri(),
                unidad.getEstado(),
                unidad.getEstado().getDescripcion(),
                unidad.getCreadorId(),
                unidad.getCreadaEn());
    }
}
