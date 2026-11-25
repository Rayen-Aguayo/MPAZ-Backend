package com.example.evaluacion_service.model;

import jakarta.persistence.*;
import lombok.*;

/** Desafío de práctica ya resuelto correctamente por un estudiante. */
@Entity
@Table(name = "avance_desafios")
@Getter
@Setter
@NoArgsConstructor
public class AvanceDesafio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long estudianteId;
    private Long preguntaId;
    private Long unidadId;
    private Long fecha;
}
