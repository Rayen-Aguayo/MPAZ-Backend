package com.example.unidad_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "preguntas")
@Getter
@Setter
@NoArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long unidadId;
    private String texto;
    private String alternativaA;
    private String alternativaB;
    private String alternativaC;
    private String alternativaD;
    /** 0 = A, 1 = B, 2 = C, 3 = D */
    private Integer correcta;
    /** false = desafío de práctica, true = pregunta de la prueba final */
    private Boolean esPruebaFinal;
    private Integer orden;
}
