package com.example.evaluacion_service.model;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "observaciones_pedagogicas",
        indexes = @Index(name = "idx_obs_est_unidad", columnList = "estudiante_id, unidad_id")
)
public class ObservacionPedagogica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "estudiante_id", nullable = false)
    private Long estudianteId;

    @Column(name = "unidad_id", nullable = false)
    private Long unidadId;

    @Column(name = "docente_id", nullable = false)
    private Long docenteId;

    @Column(nullable = false, length = 2000)
    private String texto;

    @Column(nullable = false)
    private OffsetDateTime fecha = OffsetDateTime.now();

    protected ObservacionPedagogica() {
    }

    public ObservacionPedagogica(Long e, Long u, Long d, String t) {
        estudianteId = e;
        unidadId = u;
        docenteId = d;
        texto = t;
    }

    public Long getId() {
        return id;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public Long getUnidadId() {
        return unidadId;
    }

    public Long getDocenteId() {
        return docenteId;
    }

    public String getTexto() {
        return texto;
    }

    public OffsetDateTime getFecha() {
        return fecha;
    }
}