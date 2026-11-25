package com.example.unidad_service.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.unidad_service.dto.PreguntaDto;
import com.example.unidad_service.dto.UnidadDto;
import com.example.unidad_service.dto.UnidadRequest;
import com.example.unidad_service.model.EstadoUnidad;
import com.example.unidad_service.model.Pregunta;
import com.example.unidad_service.model.Unidad;
import com.example.unidad_service.repository.PreguntaRepository;
import com.example.unidad_service.repository.UnidadRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnidadService {

    private final UnidadRepository unidades;
    private final PreguntaRepository preguntas;

    // ---------- Unidades ----------

    public UnidadDto crear(UnidadRequest r, Long creadorId) {
        Unidad u = new Unidad();
        copiar(r, u);
        u.setEstado(EstadoUnidad.BORRADOR);
        u.setCreadorId(creadorId);
        u.setCreadaEn(System.currentTimeMillis());
        return aDto(unidades.save(u));
    }

    public UnidadDto editar(Long id, UnidadRequest r) {
        Unidad u = buscar(id);
        if (!u.getEstado().sePuedeEditar()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "Una unidad autorizada no se edita: primero hay que deshabilitarla");
        }
        copiar(r, u);
        return aDto(unidades.save(u));
    }

    /** UTP ve todas (filtrables por estado); docente y estudiante solo las AUTORIZADAS. */
    public List<UnidadDto> listar(String role, String estado) {
        List<Unidad> lista;
        if (!"UTP".equals(role)) {
            lista = unidades.findByEstado(EstadoUnidad.AUTORIZADA);
        } else if (estado != null && !estado.isBlank()) {
            lista = unidades.findByEstado(parseEstado(estado));
        } else {
            lista = unidades.findAll();
        }
        return lista.stream().map(this::aDto).toList();
    }

    public UnidadDto obtener(Long id, String role) {
        return aDto(visiblePara(id, role));
    }

    @Transactional
    public UnidadDto cambiarEstado(Long id, String nuevoTexto) {
        Unidad u = buscar(id);
        EstadoUnidad nuevo = parseEstado(nuevoTexto);
        if (!u.getEstado().siguientesPermitidos().contains(nuevo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No se puede pasar de " + u.getEstado() + " a " + nuevo);
        }
        if (nuevo == EstadoUnidad.EN_REVISION) {
            if (preguntas.countByUnidadIdAndEsPruebaFinal(id, false) == 0
                    || preguntas.countByUnidadIdAndEsPruebaFinal(id, true) == 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Para enviar a revisión la unidad necesita al menos un desafío y una pregunta de la prueba final");
            }
        }
        u.setEstado(nuevo);
        return aDto(unidades.save(u));
    }

    @Transactional
    public void eliminar(Long id) {
        Unidad u = buscar(id);
        if (u.getEstado() == EstadoUnidad.AUTORIZADA) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar una unidad autorizada");
        }
        preguntas.deleteByUnidadId(id);
        unidades.delete(u);
    }

    public List<UnidadDto> porIds(List<Long> ids) {
        return unidades.findAllById(ids).stream().map(this::aDto).toList();
    }

    // ---------- Utilidades compartidas con PreguntaService ----------

    public Unidad buscar(Long id) {
        return unidades.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada"));
    }

    /** UTP ve cualquiera; el resto solo si está AUTORIZADA. */
    public Unidad visiblePara(Long id, String role) {
        Unidad u = buscar(id);
        if (!"UTP".equals(role) && u.getEstado() != EstadoUnidad.AUTORIZADA) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidad no encontrada");
        }
        return u;
    }

    public static PreguntaDto aPreguntaDto(Pregunta p, boolean conRespuesta) {
        return new PreguntaDto(p.getId(), p.getUnidadId(), p.getTexto(),
            List.of(p.getAlternativaA(), p.getAlternativaB(), p.getAlternativaC(), p.getAlternativaD()),
            conRespuesta ? p.getCorrecta() : null,
            Boolean.TRUE.equals(p.getEsPruebaFinal()), p.getOrden() == null ? 0 : p.getOrden());
    }

    private EstadoUnidad parseEstado(String texto) {
        try {
            return EstadoUnidad.valueOf(texto.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Estado inválido: BORRADOR, EN_REVISION, AUTORIZADA o DESHABILITADA");
        }
    }

    private void copiar(UnidadRequest r, Unidad u) {
        u.setAsignatura(r.asignatura().trim());
        u.setTitulo(r.titulo().trim());
        u.setCodigoOA(r.codigoOA().trim());
        u.setObjetivo(r.objetivo().trim());
        u.setExplicacion(r.explicacion().trim());
        u.setEjemplo(r.ejemplo().trim());
        u.setInstrucciones(r.instrucciones() == null ? "" : r.instrucciones().trim());
        u.setImagenUri(r.imagenUri());
    }

    private UnidadDto aDto(Unidad u) {
        return new UnidadDto(u.getId(), u.getAsignatura(), u.getTitulo(), u.getCodigoOA(), u.getObjetivo(),
            u.getExplicacion(), u.getEjemplo(), u.getInstrucciones(), u.getImagenUri(),
            u.getEstado().name(), u.getCreadorId(), u.getCreadaEn(),
            preguntas.countByUnidadIdAndEsPruebaFinal(u.getId(), false),
            preguntas.countByUnidadIdAndEsPruebaFinal(u.getId(), true));
    }
}
