package com.example.unidad_service.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** DTO de entrada para crear o actualizar una unidad didáctica. */
public record UnidadRequestDTO(
        @NotBlank(message = "La asignatura es obligatoria")
                @Size(max = 80, message = "La asignatura no puede superar los 80 caracteres")
                String asignatura,
        @NotBlank(message = "El título de la unidad es obligatorio")
                @Size(min = 5, max = 60, message = "El título debe tener entre 5 y 60 caracteres")
                String titulo,
        @NotBlank(message = "El código OA es obligatorio")
                @Pattern(
                        regexp = "^(?i)OA\s?\d{1,2}$",
                        message = "Formato inválido. Debe ser como 'OA 3'")
                String codigoOA,
        @NotBlank(message = "El objetivo de aprendizaje es obligatorio")
                @Size(
                        min = 10,
                        max = 150,
                        message = "El objetivo debe tener entre 10 y 150 caracteres")
                String objetivo,
        @NotBlank(message = "La explicación escrita es obligatoria")
                @Size(
                        min = 20,
                        max = 300,
                        message = "La explicación debe tener entre 20 y 300 caracteres")
                String explicacion,
        @NotBlank(message = "El ejemplo guiado es obligatorio")
                @Size(
                        min = 5,
                        max = 200,
                        message = "El ejemplo debe tener entre 5 y 200 caracteres")
                String ejemplo,
        @NotBlank(message = "Las instrucciones son obligatorias")
                @Size(
                        min = 10,
                        max = 200,
                        message = "Las instrucciones deben tener entre 10 y 200 caracteres")
                String instrucciones,
        @Size(max = 500, message = "La URI de la imagen no puede superar los 500 caracteres")
                String imagenUri,
        @NotNull(message = "El identificador del creador (UTP) es obligatorio") Long creadorId) {}
