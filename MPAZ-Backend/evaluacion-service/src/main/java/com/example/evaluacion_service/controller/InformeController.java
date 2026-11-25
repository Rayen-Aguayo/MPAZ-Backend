package com.example.evaluacion_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.example.evaluacion_service.dto.AvanceEstudiante;
import com.example.evaluacion_service.dto.IntentoDto;
import com.example.evaluacion_service.dto.PromedioCurso;
import com.example.evaluacion_service.dto.ResumenOA;
import com.example.evaluacion_service.repository.IntentoPruebaRepository;
import com.example.evaluacion_service.service.PruebaService;

import lombok.RequiredArgsConstructor;

/** Para docente y UTP (el gateway lo restringe). */
@RestController
@RequiredArgsConstructor
public class InformeController {

    private final IntentoPruebaRepository intentos;
    private final PruebaService pruebas;

    /** Seguimiento por asignación: intentos y mejor nota de cada estudiante. */
    @GetMapping("/informes/asignacion/{asignacionId}")
    public List<AvanceEstudiante> porAsignacion(@PathVariable Long asignacionId) {
        return intentos.avancePorAsignacion(asignacionId);
    }

    /** Informe de un estudiante en una unidad (intentos con respuestas marcadas). */
    @GetMapping("/informes/estudiante/{estudianteId}/unidad/{unidadId}")
    public List<IntentoDto> porEstudiante(@PathVariable Long estudianteId, @PathVariable Long unidadId) {
        return pruebas.informeEstudiante(estudianteId, unidadId);
    }

    /** Rendimiento por objetivo de aprendizaje; ?curso= filtra por curso. */
    @GetMapping("/rendimiento")
    public List<ResumenOA> rendimiento(@RequestParam(required = false) String curso) {
        return (curso == null || curso.isBlank())
            ? intentos.resumenPorOA()
            : intentos.resumenPorOAyCurso(curso);
    }

    /** Promedio de notas por curso. */
    @GetMapping("/rendimiento/cursos")
    public List<PromedioCurso> promedioCursos() {
        return intentos.promedioPorCurso();
    }
}
