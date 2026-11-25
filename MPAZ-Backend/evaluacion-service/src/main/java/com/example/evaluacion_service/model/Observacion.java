package com.example.evaluacion_service.model;

import jakarta.persistence.*;
import lombok.*;

/** Observación pedagógica del docente sobre el informe de un estudiante en una unidad. */
@Entity
@Table(name = "observaciones")
@Getter
@Setter
@NoArgsConstructor
public class Observacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long estudianteId;
    private Long unidadId;
    private Long docenteId;
    private String texto;
    private Long fecha;
}
