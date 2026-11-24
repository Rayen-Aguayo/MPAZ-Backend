package com.example.evaluacion_service.service;

import com.example.evaluacion_service.DTO.*;
import com.example.evaluacion_service.client.*;
import com.example.evaluacion_service.model.*;
import com.example.evaluacion_service.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EvaluacionService {

    private final EvaluacionRepository evaluaciones;
    private final IntentoEvaluacionRepository intentos;
    private final ObservacionPedagogicaRepository observaciones;
    private final UnidadClient unidadClient;
    private final AsignacionClient asignacionClient;
    private final CalificacionService calificacionService;

    public EvaluacionService(
            EvaluacionRepository e,
            IntentoEvaluacionRepository i,
            ObservacionPedagogicaRepository o,
            UnidadClient u,
            AsignacionClient a,
            CalificacionService c
    ) {
        evaluaciones = e;
        intentos = i;
        observaciones = o;
        unidadClient = u;
        asignacionClient = a;
        calificacionService = c;
    }

    @Transactional
    public EvaluacionResponse crear(CrearEvaluacionRequest r) {
        if (evaluaciones.findByAsignacionId(r.asignacionId()).isPresent()) {
            throw new ReglaNegocioException("Ya existe una evaluación para esta asignación.", HttpStatus.CONFLICT);
        }
        return EvaluacionResponse.from(
                evaluaciones.save(
                        new Evaluacion(
                                r.unidadId(),
                                r.asignacionId(),
                                r.titulo(),
                                r.porcentajeExigencia(),
                                r.puntajeMaximo()
                        )
                )
        );
    }

    @Transactional(readOnly = true)
    public EvaluacionResponse obtener(Long id) {
        return EvaluacionResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public List<EvaluacionResponse> listar() {
        return evaluaciones.findAll().stream()
                .map(EvaluacionResponse::from)
                .toList();
    }

    @Transactional
    public IntentoResponse iniciar(Long evaluacionId, Long estudianteId) {
        Evaluacion e = buscar(evaluacionId);
        if (e.getEstado() != EstadoEvaluacion.ACTIVA) {
            throw new ReglaNegocioException("La evaluación no está activa.", HttpStatus.CONFLICT);
        }
        AsignacionClient.AsignacionRemota a = asignacionClient.obtenerAsignacion(e.getAsignacionId());
        if (a == null || !Objects.equals(a.unidadId(), e.getUnidadId())) {
            throw new ReglaNegocioException("La asignación no existe o no corresponde a la unidad.", HttpStatus.BAD_GATEWAY);
        }
        if (a.maxIntentos() == null || a.maxIntentos() < 1) {
            throw new ReglaNegocioException("La asignación no tiene un máximo de intentos válido.", HttpStatus.BAD_GATEWAY);
        }
        if (a.estado() != null && !a.estado().equalsIgnoreCase("ACTIVA")) {
            throw new ReglaNegocioException("La asignación no está activa.", HttpStatus.CONFLICT);
        }
        long usados = intentos.countByEvaluacionIdAndEstudianteId(evaluacionId, estudianteId);
        if (usados >= a.maxIntentos()) {
            throw new ReglaNegocioException("El estudiante alcanzó el máximo de intentos permitido.", HttpStatus.CONFLICT);
        }
        List<UnidadClient.PreguntaRemota> preguntas = unidadClient.obtenerPreguntasPruebaFinal(e.getUnidadId());
        if (preguntas.isEmpty()) {
            throw new ReglaNegocioException("La unidad no tiene preguntas de prueba final.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (preguntas.stream().anyMatch(p -> p.id() == null || p.correcta() == null || p.correcta() < 0 || p.correcta() > 3)) {
            throw new ReglaNegocioException("unidad-service entregó preguntas con claves inválidas.", HttpStatus.BAD_GATEWAY);
        }
        IntentoEvaluacion intento = new IntentoEvaluacion(e, estudianteId, (int) usados + 1, preguntas.size());
        return IntentoResponse.from(intentos.save(intento));
    }

    @Transactional
    public IntentoResponse finalizar(Long intentoId, FinalizarIntentoRequest request) {
        IntentoEvaluacion intento = intentos.findById(intentoId)
                .orElseThrow(() -> new ReglaNegocioException("Intento no encontrado.", HttpStatus.NOT_FOUND));
        if (intento.getEstado() != EstadoIntento.EN_CURSO) {
            throw new ReglaNegocioException("El intento ya fue finalizado.", HttpStatus.CONFLICT);
        }
        List<UnidadClient.PreguntaRemota> preguntas = unidadClient.obtenerPreguntasPruebaFinal(
                intento.getEvaluacion().getUnidadId()
        );
        Map<Long, Integer> claves = preguntas.stream()
                .collect(Collectors.toMap(UnidadClient.PreguntaRemota::id, UnidadClient.PreguntaRemota::correcta));
        Set<Long> ids = new HashSet<>();
        if (request.respuestas().size() != intento.getTotalPreguntas()) {
            throw new ReglaNegocioException("Debe responder todas las preguntas de la prueba.", HttpStatus.BAD_REQUEST);
        }
        int aciertos = 0;
        for (RespuestaRequest r : request.respuestas()) {
            if (!ids.add(r.preguntaId())) {
                throw new ReglaNegocioException("No se permiten preguntas repetidas.", HttpStatus.BAD_REQUEST);
            }
            Integer correcta = claves.get(r.preguntaId());
            if (correcta == null) {
                throw new ReglaNegocioException("La pregunta no pertenece a la prueba de esta unidad.", HttpStatus.BAD_REQUEST);
            }
            boolean ok = correcta.equals(r.elegida());
            if (ok) {
                aciertos++;
            }
            intento.agregarRespuesta(new RespuestaEvaluacion(intento, r.preguntaId(), r.elegida(), ok));
        }
        BigDecimal nota = calificacionService.calcularNota(
                aciertos,
                intento.getTotalPreguntas(),
                intento.getEvaluacion().getPorcentajeExigencia()
        );
        int puntaje = (int) Math.round(
                (double) aciertos * intento.getEvaluacion().getPuntajeMaximo() / intento.getTotalPreguntas()
        );
        intento.finalizar(aciertos, puntaje, nota);
        return IntentoResponse.from(intentos.save(intento));
    }

    @Transactional(readOnly = true)
    public List<IntentoResponse> intentosDeAlumno(Long evaluacionId, Long estudianteId) {
        buscar(evaluacionId);
        return intentos.findByEvaluacionIdAndEstudianteIdOrderByFechaInicioDesc(evaluacionId, estudianteId).stream()
                .map(IntentoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RespuestaDetalleResponse> respuestas(Long intentoId) {
        IntentoEvaluacion i = intentos.findById(intentoId)
                .orElseThrow(() -> new ReglaNegocioException("Intento no encontrado.", HttpStatus.NOT_FOUND));
        if (i.getEstado() == EstadoIntento.EN_CURSO) {
            throw new ReglaNegocioException(
                    "Las respuestas se pueden revisar una vez finalizado el intento.",
                    HttpStatus.CONFLICT
            );
        }
        return i.getRespuestas().stream()
                .map(r -> new RespuestaDetalleResponse(r.getPreguntaId(), r.getElegida(), r.getCorrecta()))
                .toList();
    }

    @Transactional
    public ObservacionResponse crearObservacion(ObservacionRequest r) {
        return ObservacionResponse.from(
                observaciones.save(
                        new ObservacionPedagogica(r.estudianteId(), r.unidadId(), r.docenteId(), r.texto())
                )
        );
    }

    @Transactional(readOnly = true)
    public List<ObservacionResponse> observaciones(Long estudianteId, Long unidadId) {
        List<ObservacionPedagogica> lista = unidadId == null
                ? observaciones.findByEstudianteIdOrderByFechaDesc(estudianteId)
                : observaciones.findByEstudianteIdAndUnidadIdOrderByFechaDesc(estudianteId, unidadId);
        return lista.stream()
                .map(ObservacionResponse::from)
                .toList();
    }

    private Evaluacion buscar(Long id) {
        return evaluaciones.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("Evaluación no encontrada.", HttpStatus.NOT_FOUND));
    }
}