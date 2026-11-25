package com.example.evaluacion_service.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.evaluacion_service.client.AsignacionClient;
import com.example.evaluacion_service.client.UnidadClient;
import com.example.evaluacion_service.dto.AsignacionDto;
import com.example.evaluacion_service.dto.PreguntaConEstado;
import com.example.evaluacion_service.dto.PreguntaDto;
import com.example.evaluacion_service.dto.ResultadoDesafio;
import com.example.evaluacion_service.model.AvanceDesafio;
import com.example.evaluacion_service.repository.AvanceDesafioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DesafioService {

    private final UnidadClient unidades;
    private final AsignacionClient asignaciones;
    private final AvanceDesafioRepository avance;

    /** El estudiante responde un desafío; deben hacerse en orden (cada uno desbloquea el siguiente). */
    public ResultadoDesafio resolver(Long estudianteId, String curso, Long preguntaId, int elegida) {
        PreguntaConEstado pc = unidades.pregunta(preguntaId);
        PreguntaDto pregunta = pc.pregunta();

        if (pregunta.esPruebaFinal()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esa pregunta es de la prueba final, no un desafío");
        }
        if (!"AUTORIZADA".equals(pc.estadoUnidad())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La unidad no está disponible");
        }
        AsignacionDto asig = asignaciones.buscar(pregunta.unidadId(), curso);
        if (asig == null || !asig.vigente()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta unidad no está disponible para tu curso");
        }

        List<PreguntaDto> desafios = unidades.preguntas(pregunta.unidadId(), "DESAFIO").stream()
            .sorted(Comparator.comparingInt(PreguntaDto::orden)).toList();
        int pos = -1;
        for (int i = 0; i < desafios.size(); i++) {
            if (desafios.get(i).id().equals(preguntaId)) pos = i;
        }
        if (pos > 0 && !avance.existsByEstudianteIdAndPreguntaId(estudianteId, desafios.get(pos - 1).id())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Desafío bloqueado: completa el anterior primero");
        }

        boolean correcta = pregunta.correcta() != null && pregunta.correcta() == elegida;
        if (correcta && !avance.existsByEstudianteIdAndPreguntaId(estudianteId, preguntaId)) {
            AvanceDesafio a = new AvanceDesafio();
            a.setEstudianteId(estudianteId);
            a.setPreguntaId(preguntaId);
            a.setUnidadId(pregunta.unidadId());
            a.setFecha(System.currentTimeMillis());
            avance.save(a);
        }
        boolean hayMas = pos >= 0 && pos + 1 < desafios.size();
        return new ResultadoDesafio(correcta,
            (correcta && hayMas) ? desafios.get(pos + 1).id() : null,
            correcta && !hayMas);
    }

    /** Ids de los desafíos que el estudiante ya completó en una unidad. */
    public List<Long> completados(Long estudianteId, Long unidadId) {
        return avance.findByEstudianteIdAndUnidadId(estudianteId, unidadId).stream()
            .map(AvanceDesafio::getPreguntaId).toList();
    }
}
