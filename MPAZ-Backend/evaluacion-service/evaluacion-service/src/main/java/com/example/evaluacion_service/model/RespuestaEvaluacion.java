package com.example.evaluacion_service.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "respuestas_evaluacion",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_respuesta_intento_pregunta",
                columnNames = {"intento_id", "pregunta_id"}
        )
)
public class RespuestaEvaluacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intento_id", nullable = false)
    private IntentoEvaluacion intento;

    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;

    /** 0=A, 1=B, 2=C, 3=D; misma convención que MPAZ PRO. */
    @Column(nullable = false)
    private Integer elegida;

    @Column(nullable = false)
    private Boolean correcta;

    protected RespuestaEvaluacion() {
    }

    public RespuestaEvaluacion(IntentoEvaluacion intento, Long preguntaId, Integer elegida, Boolean correcta) {
        this.intento = intento;
        this.preguntaId = preguntaId;
        this.elegida = elegida;
        this.correcta = correcta;
    }

    public Long getId() {
        return id;
    }

    public Long getPreguntaId() {
        return preguntaId;
    }

    public Integer getElegida() {
        return elegida;
    }

    public Boolean getCorrecta() {
        return correcta;
    }
}