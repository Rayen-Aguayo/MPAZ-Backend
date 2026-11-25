package com.example.evaluacion_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "respuestas_prueba")
@Getter
@Setter
@NoArgsConstructor
public class RespuestaPrueba {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long intentoId;
    private Long preguntaId;
    /** 0 a 3; null si no respondió */
    private Integer elegida;
    private Boolean correcta;
}
