# MPAZ-Backend

MPAZ-Backend – Microservicios de MPAZ PRO

1. Descripción

Backend de MPAZ PRO 2º con arquitectura de microservicios. Se entra siempre por el api-gateway, que valida el token JWT y autoriza por rol (UTP, DOCENTE y ESTUDIANTE).

Microservicios:

api-gateway (9090): punto de entrada, validación del token y permisos por rol.
auth-service (9094): registro, login y emisión del JWT.
unidad-service (9091): unidades, desafíos, preguntas de la prueba y estados de autorización.
asignacion-service (9092): asignación de unidades a cursos por el docente.
evaluacion-service (9093): avance, intentos, notas, observaciones e informes.

Cada servicio tiene su propia base de datos (bd_auth, bd_unidad, bd_asignacion, bd_evaluacion) y su README con el detalle de sus endpoints.

2. Tecnologías

Java 21
Spring Boot 4.0.10
Spring Web / Spring Cloud Gateway
Spring Data JPA
MySQL
Liquibase
Maven
Docker y Docker Compose
JWT (jjwt 0.12.5)

3. URL base

http://localhost:9090

4. Estructura

MPAZ-Backend/
api-gateway/
auth-service/
unidad-service/
asignacion-service/
evaluacion-service/
docker-compose.yml

Cada carpeta se crea en https://start.spring.io con: Maven, Java, Spring Boot 4.0.10, Jar, Properties, Java 21 y Group com.example. Después se copian encima el src/ y el Dockerfile de este proyecto.

Dependencias del Initializr:

api-gateway: Reactive Gateway, Spring Security, OAuth2 Resource Server.
auth-service: Spring Web, Spring Data JPA, MySQL Driver, Lombok, Validation, Liquibase Migration.
unidad-service, asignacion-service y evaluacion-service: Spring Web, Spring Data JPA, MySQL Driver, Liquibase Migration, Lombok, Validation, Spring Boot Actuator.

Solo en auth-service, agregar al pom.xml (dentro de dependencies):

```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.5</version>
    <scope>runtime</scope>
</dependency>
```

5. Ejecución

Con MySQL encendido (usuario root, sin contraseña, puerto 3306):

En cada servicio generar el jar:

```bash
./mvnw clean package -DskipTests
```

En la raíz de MPAZ-Backend levantar todo:

```bash
docker compose up -d --build
```

Solo el api-gateway (9090) queda expuesto; los demás servicios se comunican entre sí dentro de la red de Docker.

Sin Docker: cada servicio se puede ejecutar desde IntelliJ (usa localhost por defecto). Orden sugerido: unidad-service, asignacion-service, evaluacion-service, auth-service y api-gateway.

6. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar los cinco microservicios.
POST /auth/register para crear un usuario UTP, un DOCENTE y un ESTUDIANTE (con curso).
POST /auth/login con cada uno y guardar su token.
Como UTP: crear una unidad, sus preguntas, enviarla a EN_REVISION y luego a AUTORIZADA.
Como DOCENTE: asignar la unidad al curso (POST /asignaciones).
Como ESTUDIANTE: ver GET /asignaciones/mias, resolver los desafíos y entregar la prueba.
Como DOCENTE: revisar /informes, /rendimiento y crear una observación.

Ejemplo de registro:

```bash
curl -X POST http://localhost:9090/auth/register -H "Content-Type: application/json" \
  -d '{"nombre":"Ana UTP","username":"utp1","password":"1234","role":"UTP"}'
```

7. Integración entre microservicios

El token se envía en todas las peticiones (menos login y registro) con el encabezado:

```http
Authorization: Bearer TU_TOKEN
```

El api-gateway valida el token, revisa el rol según la ruta y reenvía a cada servicio los encabezados X-User-Id, X-User-Role y X-User-Curso.

Los servicios se consultan entre sí por rutas /internal/**, que el gateway bloquea para el exterior.
