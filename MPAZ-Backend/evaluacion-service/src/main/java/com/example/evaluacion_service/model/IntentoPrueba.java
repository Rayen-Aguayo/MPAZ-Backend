package com.example.evaluacion_service.model;

import jakarta.persistence.*;
import lombok.*;

/** Un intento completo de la prueba final. Guarda copia de los datos de la unidad para los informes. */
@Entity
@Table(name = "intentos_prueba")
@Getter
@Setter
@NoArgsConstructor
public class IntentoPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long asignacionId;
    private Long estudianteId;
    private Long unidadId;
    private String curso;
    private String codigoOA;
    @Column(columnDefinition = "TEXT")
    private String objetivo;
    private String asignatura;
    private Integer aciertos;
    private Integer total;
    /** Escala 1,0 a 7,0 */
    private Double nota;
    private Long fecha;
}
