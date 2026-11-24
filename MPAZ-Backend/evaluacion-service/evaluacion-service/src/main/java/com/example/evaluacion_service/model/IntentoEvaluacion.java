package com.example.evaluacion_service.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "intentos_evaluacion",
        indexes = {
                @Index(name = "idx_intento_eval_est", columnList = "evaluacion_id, estudiante_id"),
                @Index(name = "idx_intento_est", columnList = "estudiante_id")
        }
)
public class IntentoEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "numero_intento", nullable = false)
    private Integer numeroIntento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoIntento estado = EstadoIntento.EN_CURSO;

    @Column(nullable = false)
    private Integer aciertos = 0;

    @Column(name = "total_preguntas", nullable = false)
    private Integer totalPreguntas;

    @Column(name = "puntaje_obtenido", nullable = false)
    private Integer puntajeObtenido = 0;

    @Column(precision = 2, scale = 1)
    private BigDecimal nota;

    @Column(name = "fecha_inicio", nullable = false)
    private OffsetDateTime fechaInicio = OffsetDateTime.now();

    @Column(name = "fecha_termino")
    private OffsetDateTime fechaTermino;

    @OneToMany(mappedBy = "intento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RespuestaEvaluacion> respuestas = new ArrayList<>();

    protected IntentoEvaluacion() {
    }

    public IntentoEvaluacion(Evaluacion e, Long estudianteId, Integer numero, Integer total) {
        this.evaluacion = e;
        this.estudianteId = estudianteId;
        this.numeroIntento = numero;
        this.totalPreguntas = total;
    }

    public void agregarRespuesta(RespuestaEvaluacion r) {
        respuestas.add(r);
    }

    public void finalizar(int aciertos, int puntaje, BigDecimal nota) {
        this.aciertos = aciertos;
        this.puntajeObtenido = puntaje;
        this.nota = nota;
        this.estado = EstadoIntento.FINALIZADO;
        this.fechaTermino = OffsetDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Evaluacion getEvaluacion() {
        return evaluacion;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public Integer getNumeroIntento() {
        return numeroIntento;
    }

    public EstadoIntento getEstado() {
        return estado;
    }

    public Integer getAciertos() {
        return aciertos;
    }

    public Integer getTotalPreguntas() {
        return totalPreguntas;
    }

    public Integer getPuntajeObtenido() {
        return puntajeObtenido;
    }

    public BigDecimal getNota() {
        return nota;
    }

    public OffsetDateTime getFechaInicio() {
        return fechaInicio;
    }

    public OffsetDateTime getFechaTermino() {
        return fechaTermino;
    }

    public List<RespuestaEvaluacion> getRespuestas() {
        return respuestas;
    }
}