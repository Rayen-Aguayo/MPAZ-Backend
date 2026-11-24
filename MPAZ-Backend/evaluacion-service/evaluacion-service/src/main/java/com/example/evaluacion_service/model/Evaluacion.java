package com.example.evaluacion_service.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "evaluaciones",
        indexes = @Index(name = "idx_eval_asignacion", columnList = "asignacion_id")
)
public class Evaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "unidad_id", nullable = false)
    private Long unidadId;

    @Column(name = "asignacion_id", nullable = false, unique = true)
    private Long asignacionId;

    @Column(nullable = false, length = 160)
    private String titulo;

    @Column(name = "porcentaje_exigencia", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeExigencia = new BigDecimal("60.00");

    @Column(name = "puntaje_maximo", nullable = false)
    private Integer puntajeMaximo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoEvaluacion estado = EstadoEvaluacion.ACTIVA;

    @Column(name = "creada_en", nullable = false)
    private OffsetDateTime creadaEn = OffsetDateTime.now();

    @OneToMany(mappedBy = "evaluacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntentoEvaluacion> intentos = new ArrayList<>();

    protected Evaluacion() {
    }

    public Evaluacion(Long unidadId, Long asignacionId, String titulo, BigDecimal exigencia, Integer puntajeMaximo) {
        this.unidadId = unidadId;
        this.asignacionId = asignacionId;
        this.titulo = titulo;
        this.porcentajeExigencia = exigencia;
        this.puntajeMaximo = puntajeMaximo;
    }

    public Long getId() {
        return id;
    }

    public Long getUnidadId() {
        return unidadId;
    }

    public Long getAsignacionId() {
        return asignacionId;
    }

    public String getTitulo() {
        return titulo;
    }

    public BigDecimal getPorcentajeExigencia() {
        return porcentajeExigencia;
    }

    public Integer getPuntajeMaximo() {
        return puntajeMaximo;
    }

    public EstadoEvaluacion getEstado() {
        return estado;
    }

    public OffsetDateTime getCreadaEn() {
        return creadaEn;
    }

    public List<IntentoEvaluacion> getIntentos() {
        return intentos;
    }

    public void cerrar() {
        this.estado = EstadoEvaluacion.CERRADA;
    }
}