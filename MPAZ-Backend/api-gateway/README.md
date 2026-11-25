API Gateway – Puerta de entrada de MPAZ PRO

1. Descripción

El api-gateway es el único punto de entrada al backend (puerto 9090). Sus funciones son:

Enrutar cada petición al microservicio correspondiente.
Validar el token JWT (excepto en /auth/login y /auth/register).
Autorizar por rol según la ruta (UTP, DOCENTE, ESTUDIANTE).
Reenviar la identidad del usuario a los servicios con los encabezados X-User-Id, X-User-Role y X-User-Curso (los que envíe el cliente se descartan).
Bloquear /internal/**.

2. Tecnologías

Java 21
Spring Boot 4.0.10
Spring Cloud Gateway (reactivo)
Spring Security
OAuth2 Resource Server (JWT)
Maven

3. URL base

http://localhost:9090

4. Rutas

/auth/** -> auth-service (puerto 9094)
/unidades/** -> unidad-service (puerto 9091)
/asignaciones/** -> asignacion-service (puerto 9092)
/desafios/**, /pruebas/**, /informes/**, /rendimiento/**, /observaciones/** -> evaluacion-service (puerto 9093)

Permisos por rol:

POST /auth/login y /auth/register: público.
GET /auth/estudiantes: DOCENTE, UTP.
GET /unidades/**: cualquier usuario autenticado. POST, PUT, PATCH y DELETE: UTP.
POST /asignaciones, DELETE /asignaciones/{id} y GET /asignaciones/docente: DOCENTE.
GET /asignaciones/mias: ESTUDIANTE.
/desafios/** y /pruebas/**: ESTUDIANTE.
/informes/** y /rendimiento/**: DOCENTE, UTP.
POST /observaciones: DOCENTE. GET /observaciones: cualquier usuario autenticado.

Sin token válido responde 401. Con un rol que no corresponde responde 403.

5. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar los cinco microservicios.
Enviar POST /auth/login y copiar el token.
Probar una ruta protegida sin token (debe responder 401).
Probar la misma ruta con el encabezado Authorization: Bearer TU_TOKEN.
Probar una ruta de otro rol (debe responder 403).

6. Configuración

El secreto del JWT debe ser el mismo en auth-service y api-gateway. Se define con la variable SECRET (32 a 47 caracteres); si no existe, se usa el valor por defecto de application.properties.

Las URLs de los servicios se cambian con AUTH_URL, UNIDAD_URL, ASIGNACION_URL y EVALUACION_URL (el docker-compose ya las define).
