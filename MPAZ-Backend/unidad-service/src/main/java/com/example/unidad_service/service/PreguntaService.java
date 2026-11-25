package com.example.unidad_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.unidad_service.dto.PreguntaConEstado;
import com.example.unidad_service.dto.PreguntaDto;
import com.example.unidad_service.dto.PreguntaRequest;
import com.example.unidad_service.model.Pregunta;
import com.example.unidad_service.model.Unidad;
import com.example.unidad_service.repository.PreguntaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PreguntaService {

    private final PreguntaRepository preguntas;
    private final UnidadService unidadService;

    public PreguntaDto crear(Long unidadId, PreguntaRequest r) {
        Unidad u = unidadService.buscar(unidadId);
        verificarEditable(u);
        Pregunta p = new Pregunta();
        p.setUnidadId(unidadId);
        copiar(r, p);
        if (r.orden() == null) {
            p.setOrden(preguntas.maxOrden(unidadId, r.esPruebaFinal()) + 1);
        }
        return UnidadService.aPreguntaDto(preguntas.save(p), true);
    }

    public PreguntaDto editar(Long unidadId, Long preguntaId, PreguntaRequest r) {
        verificarEditable(unidadService.buscar(unidadId));
        Pregunta p = delaUnidad(unidadId, preguntaId);
        copiar(r, p);
        return UnidadService.aPreguntaDto(preguntas.save(p), true);
    }

    public void eliminar(Long unidadId, Long preguntaId) {
        verificarEditable(unidadService.buscar(unidadId));
        preguntas.delete(delaUnidad(unidadId, preguntaId));
    }

    /** tipo = DESAFIO | PRUEBA | null (todas). Estudiante no recibe la respuesta correcta. */
    public List<PreguntaDto> listar(Long unidadId, String tipo, String role) {
        unidadService.visiblePara(unidadId, role);
        return listarInterno(unidadId, tipo, !"ESTUDIANTE".equals(role));
    }

    // ---------- Uso interno (otros servicios) ----------

    public List<PreguntaDto> listarInterno(Long unidadId, String tipo, boolean conRespuesta) {
        List<Pregunta> lista;
        if (tipo == null || tipo.isBlank()) {
            lista = preguntas.findByUnidadIdOrderByEsPruebaFinalAscOrdenAsc(unidadId);
        } else {
            boolean prueba = switch (tipo.trim().toUpperCase()) {
                case "PRUEBA" -> true;
                case "DESAFIO" -> false;
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tipo debe ser DESAFIO o PRUEBA");
            };
            lista = preguntas.findByUnidadIdAndEsPruebaFinalOrderByOrdenAsc(unidadId, prueba);
        }
        return lista.stream().map(p -> UnidadService.aPreguntaDto(p, conRespuesta)).toList();
    }

    public PreguntaConEstado preguntaConEstado(Long preguntaId) {
        Pregunta p = preguntas.findById(preguntaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        Unidad u = unidadService.buscar(p.getUnidadId());
        return new PreguntaConEstado(UnidadService.aPreguntaDto(p, true), u.getEstado().name());
    }

    // ---------- Privados ----------

    private void verificarEditable(Unidad u) {
        if (!u.getEstado().sePuedeEditar()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Una unidad autorizada no se edita: primero hay que deshabilitarla");
        }
    }

    private Pregunta delaUnidad(Long unidadId, Long preguntaId) {
        Pregunta p = preguntas.findById(preguntaId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pregunta no encontrada"));
        if (!p.getUnidadId().equals(unidadId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pregunta no pertenece a esa unidad");
        }
        return p;
    }

    private void copiar(PreguntaRequest r, Pregunta p) {
        p.setTexto(r.texto().trim());
        p.setAlternativaA(r.alternativaA().trim());
        p.setAlternativaB(r.alternativaB().trim());
        p.setAlternativaC(r.alternativaC().trim());
        p.setAlternativaD(r.alternativaD().trim());
        p.setCorrecta(r.correcta());
        p.setEsPruebaFinal(r.esPruebaFinal());
        if (r.orden() != null) {
            p.setOrden(r.orden());
        }
    }
}
