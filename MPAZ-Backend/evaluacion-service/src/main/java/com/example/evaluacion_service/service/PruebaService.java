package com.example.evaluacion_service.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.evaluacion_service.client.AsignacionClient;
import com.example.evaluacion_service.client.UnidadClient;
import com.example.evaluacion_service.dto.AsignacionDto;
import com.example.evaluacion_service.dto.DetalleRespuesta;
import com.example.evaluacion_service.dto.IntentoDto;
import com.example.evaluacion_service.dto.IntentoRequest;
import com.example.evaluacion_service.dto.MisIntentos;
import com.example.evaluacion_service.dto.PreguntaDto;
import com.example.evaluacion_service.dto.RespuestaItem;
import com.example.evaluacion_service.dto.UnidadResumen;
import com.example.evaluacion_service.model.IntentoPrueba;
import com.example.evaluacion_service.model.RespuestaPrueba;
import com.example.evaluacion_service.repository.IntentoPruebaRepository;
import com.example.evaluacion_service.repository.RespuestaPruebaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PruebaService {

    private final UnidadClient unidades;
    private final AsignacionClient asignaciones;
    private final IntentoPruebaRepository intentos;
    private final RespuestaPruebaRepository respuestas;

    /** La nota se calcula aquí; la app nunca la envía. */
    @Transactional
    public IntentoDto rendir(Long estudianteId, String curso, Long asignacionId, IntentoRequest req) {
        AsignacionDto asig = asignaciones.porId(asignacionId);
        if (asig == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada");
        }
        if (!asig.curso().equals(curso)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta prueba no es de tu curso");
        }
        if (!asig.vigente()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La prueba está fuera del plazo");
        }
        UnidadResumen unidad = unidades.unidad(asig.unidadId());
        if (!"AUTORIZADA".equals(unidad.estado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La unidad no está disponible");
        }
        long usados = intentos.countByAsignacionIdAndEstudianteId(asignacionId, estudianteId);
        if (usados >= asig.maxIntentos()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya usaste todos tus intentos");
        }
        List<PreguntaDto> prueba = unidades.preguntas(asig.unidadId(), "PRUEBA");
        if (prueba.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La unidad no tiene pregunta de prueba");
        }

        Map<Long, Integer> elegidas = new HashMap<>();
        for (RespuestaItem item : req.respuestas()) {
            elegidas.put(item.preguntaId(), item.elegida());
        }

        int aciertos = 0;
        List<RespuestaPrueba> filas = new ArrayList<>();
        List<DetalleRespuesta> detalle = new ArrayList<>();
        for (PreguntaDto p : prueba) {
            Integer elegida = elegidas.get(p.id());
            boolean ok = elegida != null && elegida.equals(p.correcta());
            if (ok) aciertos++;
            RespuestaPrueba r = new RespuestaPrueba();
            r.setPreguntaId(p.id());
            r.setElegida(elegida);
            r.setCorrecta(ok);
            filas.add(r);
            detalle.add(new DetalleRespuesta(p.id(), elegida, ok, p.correcta()));
        }

        IntentoPrueba i = new IntentoPrueba();
        i.setAsignacionId(asignacionId);
        i.setEstudianteId(estudianteId);
        i.setUnidadId(asig.unidadId());
        i.setCurso(asig.curso());
        i.setCodigoOA(unidad.codigoOA());
        i.setObjetivo(unidad.objetivo());
        i.setAsignatura(unidad.asignatura());
        i.setAciertos(aciertos);
        i.setTotal(prueba.size());
        i.setNota(Calificacion.nota(aciertos, prueba.size()));
        i.setFecha(System.currentTimeMillis());
        i = intentos.save(i);

        for (RespuestaPrueba r : filas) {
            r.setIntentoId(i.getId());
        }
        respuestas.saveAll(filas);

        int usadosAhora = (int) usados + 1;
        return aDto(i, usadosAhora, asig.maxIntentos() - usadosAhora,
            Boolean.TRUE.equals(asig.permitirVerRespuestas()) ? detalle : null);
    }

    public MisIntentos misIntentos(Long estudianteId, String curso, Long asignacionId) {
        AsignacionDto asig = asignaciones.porId(asignacionId);
        if (asig == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Asignación no encontrada");
        }
        if (!asig.curso().equals(curso)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esta prueba no es de tu curso");
        }
        List<IntentoPrueba> lista = intentos.findByAsignacionIdAndEstudianteIdOrderByFechaDesc(asignacionId, estudianteId);
        double mejor = lista.stream().mapToDouble(IntentoPrueba::getNota).max().orElse(0.0);
        int usados = lista.size();
        return new MisIntentos(usados, asig.maxIntentos(), Math.max(0, asig.maxIntentos() - usados), mejor,
            lista.stream().map(x -> aDto(x, null, null, null)).toList());
    }

    /** Informe del docente: intentos de un estudiante en una unidad, con el detalle de respuestas. */
    public List<IntentoDto> informeEstudiante(Long estudianteId, Long unidadId) {
        return intentos.findByEstudianteIdAndUnidadIdOrderByFechaDesc(estudianteId, unidadId).stream()
            .map(x -> aDto(x, null, null, respuestas.findByIntentoId(x.getId()).stream()
                .map(r -> new DetalleRespuesta(r.getPreguntaId(), r.getElegida(), r.getCorrecta(), null))
                .toList()))
            .toList();
    }

    private IntentoDto aDto(IntentoPrueba i, Integer usados, Integer restantes, List<DetalleRespuesta> detalle) {
        return new IntentoDto(i.getId(), i.getAsignacionId(), i.getEstudianteId(), i.getAciertos(), i.getTotal(),
            i.getNota(), i.getFecha(), usados, restantes, detalle);
    }
}
