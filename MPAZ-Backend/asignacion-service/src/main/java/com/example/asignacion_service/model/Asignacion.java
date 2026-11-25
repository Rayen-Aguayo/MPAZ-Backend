package com.example.asignacion_service.model;

import jakarta.persistence.*;
import lombok.*;

/** Lo que define el docente al asignar una unidad autorizada a un curso. */
@Entity
@Table(name = "asignaciones")
@Getter
@Setter
@NoArgsConstructor
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long unidadId;
    private Long docenteId;
    /** Ej.: "2° Básico A" */
    private String curso;
    /** Medianoche UTC (igual que entrega el DatePicker de la app) */
    private Long fechaInicio;
    private Long fechaFin;
    /** Máximo de intentos de la prueba final */
    private Integer maxIntentos;
    private Boolean notificarCurso;
    /** Si el estudiante puede ver qué respondió bien/mal al terminar */
    private Boolean permitirVerRespuestas;
    private Long creadaEn;
}
