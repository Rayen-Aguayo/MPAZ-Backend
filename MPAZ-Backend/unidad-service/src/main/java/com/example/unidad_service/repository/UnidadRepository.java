package com.example.unidad_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.unidad_service.model.EstadoUnidad;
import com.example.unidad_service.model.Unidad;

public interface UnidadRepository extends JpaRepository<Unidad, Long> {

    List<Unidad> findByEstado(EstadoUnidad estado);
}
