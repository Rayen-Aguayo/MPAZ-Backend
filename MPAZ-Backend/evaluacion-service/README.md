Evaluación Service – Microservicio de Evaluaciones (MPAZ PRO)

1. Descripción

El microservicio evaluacion-service registra el trabajo del estudiante y entrega los informes al docente:

Desafíos de práctica: se resuelven en orden; resolver uno bien desbloquea el siguiente.
Prueba final: valida los intentos permitidos, corrige y calcula la nota en el servidor (la app nunca envía la nota).
Observaciones pedagógicas del docente.
Informes: seguimiento por asignación, informe por estudiante, rendimiento por objetivo de aprendizaje y promedio por curso.

Nota: escala 1,0 a 7,0 con 60 % de exigencia (60 % correcto = nota 4,0).

2. Tecnologías

Java 21
Spring Boot 4.0.10
Spring Web
Spring Data JPA
MySQL
Liquibase
Maven
API REST
JSON

3. URL base

http://localhost:9090

(Se entra siempre por el api-gateway. El servicio corre en el puerto 9093; Base de datos: bd_evaluacion)

4. Endpoints

Resolver un desafío (ESTUDIANTE)

POST /desafios/{preguntaId}/resolver

```json
{
    "elegida": 1
}
```

Respuesta:

```json
{
    "correcta": true,
    "siguienteDesafioId": 5,
    "desafiosCompletos": false
}
```

Responde 409 si el desafío anterior aún no está completo.

Desafíos completados de una unidad (ESTUDIANTE): GET /desafios/unidad/{unidadId}/avance

Entregar la prueba final (ESTUDIANTE)

POST /pruebas/{asignacionId}/intentos

```json
{
    "respuestas": [
        { "preguntaId": 10, "elegida": 2 },
        { "preguntaId": 11, "elegida": 0 }
    ]
}
```

Respuesta (201):

```json
{
    "id": 1,
    "asignacionId": 1,
    "estudianteId": 3,
    "aciertos": 1,
    "total": 2,
    "nota": 3.5,
    "fecha": 1764288000000,
    "intentosUsados": 1,
    "intentosRestantes": 2,
    "detalle": [
        { "preguntaId": 10, "elegida": 2, "correcta": true, "respuestaCorrecta": 2 },
        { "preguntaId": 11, "elegida": 0, "correcta": false, "respuestaCorrecta": 3 }
    ]
}
```

El campo "detalle" solo viene si la asignación permite ver respuestas. Responde 409 si se acabaron los intentos o si está fuera de plazo.

Mis intentos (ESTUDIANTE): GET /pruebas/{asignacionId}/mis-intentos

Seguimiento por asignación (DOCENTE, UTP): GET /informes/asignacion/{asignacionId}

Informe de un estudiante en una unidad (DOCENTE, UTP): GET /informes/estudiante/{estudianteId}/unidad/{unidadId}

Rendimiento por objetivo de aprendizaje (DOCENTE, UTP): GET /rendimiento?curso=2° Básico A

Promedio por curso (DOCENTE, UTP): GET /rendimiento/cursos

Crear observación (DOCENTE)

POST /observaciones

```json
{
    "estudianteId": 3,
    "unidadId": 1,
    "texto": "Debe reforzar la lectura de cuentos."
}
```

Ver observaciones: GET /observaciones?estudianteId=3&unidadId=1 (el estudiante solo ve las suyas).

5. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar todos los microservicios y el api-gateway.
Tener una unidad AUTORIZADA asignada al curso del estudiante.
Hacer login como ESTUDIANTE y copiar el token.
Resolver los desafíos en orden (POST /desafios/{preguntaId}/resolver).
Entregar la prueba (POST /pruebas/{asignacionId}/intentos).
Hacer login como DOCENTE y consultar /informes y /rendimiento.

6. Integración con otros microservicios

evaluacion-service consulta a unidad-service (preguntas y respuestas correctas) y a asignacion-service (curso, fechas e intentos máximos) por sus rutas /internal, que el api-gateway bloquea para el exterior.

Los endpoints públicos se envían con el encabezado:

```http
Authorization: Bearer TU_TOKEN
```
