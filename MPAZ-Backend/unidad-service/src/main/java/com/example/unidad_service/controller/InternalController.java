package com.example.unidad_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.unidad_service.dto.*;
import com.example.unidad_service.service.PreguntaService;
import com.example.unidad_service.service.UnidadService;

import lombok.RequiredArgsConstructor;

/**
 * Solo para otros servicios (asignación y evaluación). El gateway NO publica /internal,
 * y en el docker-compose este servicio no expone puertos al exterior.
 */
@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
public class InternalController {

    private final UnidadService unidades;
    private final PreguntaService preguntas;

    @GetMapping("/unidades")
    public List<UnidadDto> porIds(@RequestParam List<Long> ids) {
        return unidades.porIds(ids);
    }

    @GetMapping("/unidades/{id}")
    public UnidadDto unidad(@PathVariable Long id) {
        return unidades.obtener(id, "UTP");
    }

    /** Con respuesta correcta incluida. tipo = DESAFIO | PRUEBA */
    @GetMapping("/unidades/{id}/preguntas")
    public List<PreguntaDto> preguntas(@PathVariable Long id, @RequestParam(required = false) String tipo) {
        return preguntas.listarInterno(id, tipo, true);
    }

    @GetMapping("/preguntas/{id}")
    public PreguntaConEstado pregunta(@PathVariable Long id) {
        return preguntas.preguntaConEstado(id);
    }
}
