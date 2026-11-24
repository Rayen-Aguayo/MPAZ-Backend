package com.example.unidad_service.service;

import com.example.unidad_service.DTO.CambioEstadoRequestDTO;
import com.example.unidad_service.DTO.UnidadRequestDTO;
import com.example.unidad_service.DTO.UnidadResponseDTO;
import com.example.unidad_service.model.EstadoUnidad;
import java.util.List;

public interface UnidadService {

    List<UnidadResponseDTO> listarUnidades(EstadoUnidad estado, String asignatura);

    List<UnidadResponseDTO> listarAutorizadas();

    UnidadResponseDTO obtenerPorId(Long id);

    UnidadResponseDTO crearUnidad(UnidadRequestDTO request);

    UnidadResponseDTO actualizarUnidad(Long id, UnidadRequestDTO request);

    UnidadResponseDTO cambiarEstado(Long id, CambioEstadoRequestDTO request);

    void eliminarUnidad(Long id);
}
