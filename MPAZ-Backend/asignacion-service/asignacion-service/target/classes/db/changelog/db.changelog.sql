--liquibase formatted sql

--changeset mpaz:1
CREATE TABLE asignaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    unidad_id BIGINT NOT NULL,
    docente_id BIGINT NOT NULL,
    curso VARCHAR(30) NOT NULL,
    fecha_inicio BIGINT NOT NULL,
    fecha_fin BIGINT NOT NULL,
    max_intentos INT NOT NULL,
    notificar_curso BOOLEAN NOT NULL,
    permitir_ver_respuestas BOOLEAN NOT NULL,
    creada_en BIGINT NOT NULL,
    CONSTRAINT uq_asignacion_unidad_curso UNIQUE (unidad_id, curso)
);
