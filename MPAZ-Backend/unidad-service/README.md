Unidad Service – Microservicio de Unidades (MPAZ PRO)

1. Descripción

El microservicio unidad-service gestiona las unidades didácticas que crea la UTP: sus datos, sus desafíos de práctica, las preguntas de la prueba final y su estado de autorización.

Los docentes y estudiantes solo ven las unidades AUTORIZADAS. El estudiante nunca recibe la respuesta correcta.

Estados de una unidad:

BORRADOR -> EN_REVISION -> AUTORIZADA -> DESHABILITADA
EN_REVISION puede volver a BORRADOR, y DESHABILITADA puede volver a AUTORIZADA.
Una unidad AUTORIZADA no se edita: primero hay que deshabilitarla.
Para pasar a EN_REVISION necesita al menos un desafío y una pregunta de la prueba final.

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

http://localhost:9090/unidades

(Se entra siempre por el api-gateway. El servicio corre en el puerto 9091; Base de datos: bd_unidad)

4. Endpoints

Crear unidad (UTP)

POST /unidades

```json
{
    "asignatura": "Lenguaje",
    "titulo": "Los cuentos",
    "codigoOA": "OA 3",
    "objetivo": "Leer cuentos breves",
    "explicacion": "Un cuento tiene inicio, desarrollo y final.",
    "ejemplo": "Caperucita Roja",
    "instrucciones": "Lee el texto y responde.",
    "imagenUri": null
}
```

Respuesta (201): la unidad creada, con "estado": "BORRADOR", "totalDesafios" y "totalPrueba".

Editar unidad (UTP): PUT /unidades/{id} (mismo cuerpo). Responde 409 si está AUTORIZADA.

Listar unidades: GET /unidades?estado=AUTORIZADA (la UTP ve todas; docente y estudiante solo las AUTORIZADAS).

Ver una unidad: GET /unidades/{id}

Cambiar estado (UTP)

PATCH /unidades/{id}/estado

```json
{
    "estado": "EN_REVISION"
}
```

Eliminar unidad (UTP): DELETE /unidades/{id} (no se puede si está AUTORIZADA).

Crear pregunta (UTP)

POST /unidades/{id}/preguntas

```json
{
    "texto": "¿Quién entra al bosque?",
    "alternativaA": "El lobo",
    "alternativaB": "Caperucita",
    "alternativaC": "El cazador",
    "alternativaD": "La abuela",
    "correcta": 1,
    "esPruebaFinal": false
}
```

correcta: 0 = A, 1 = B, 2 = C, 3 = D. esPruebaFinal en false es un desafío de práctica; en true es una pregunta de la prueba final. El orden se asigna solo.

Listar preguntas: GET /unidades/{id}/preguntas?tipo=DESAFIO (o PRUEBA). Si el usuario es estudiante, la respuesta no incluye "correcta".

Editar y eliminar pregunta (UTP): PUT y DELETE /unidades/{id}/preguntas/{preguntaId}

5. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar auth-service, unidad-service y api-gateway.
Hacer login como UTP y copiar el token.
Crear una unidad (POST /unidades).
Crear al menos un desafío y una pregunta de prueba final.
Enviar la unidad a EN_REVISION y luego a AUTORIZADA.
Hacer login como DOCENTE o ESTUDIANTE y consultar GET /unidades.

6. Integración con otros microservicios

unidad-service expone rutas internas (/internal/**) que consultan asignacion-service y evaluacion-service. El api-gateway las bloquea y no se pueden llamar desde fuera.

Los endpoints públicos se envían con el encabezado:

```http
Authorization: Bearer TU_TOKEN
```
