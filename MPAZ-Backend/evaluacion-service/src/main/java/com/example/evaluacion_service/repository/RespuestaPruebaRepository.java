package com.example.evaluacion_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.evaluacion_service.model.RespuestaPrueba;

public interface RespuestaPruebaRepository extends JpaRepository<RespuestaPrueba, Long> {

    List<RespuestaPrueba> findByIntentoId(Long intentoId);
}
