Auth Service – Microservicio de Autenticación (MPAZ PRO)

1. Descripción

El microservicio auth-service gestiona el registro y la autenticación de los usuarios de MPAZ PRO (UTP, docente y estudiante).

Expone los siguientes endpoints REST:

Registro: permite crear un nuevo usuario con su perfil (y su curso, si es estudiante).
Login: valida las credenciales y genera un token JWT que lleva el id, el rol y el curso del usuario.
Estudiantes: lista los estudiantes (opcionalmente de un curso) para el docente y la UTP.

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
JWT (jjwt 0.12.5)

3. URL base

http://localhost:9090/auth

(Se entra siempre por el api-gateway. El servicio corre en el puerto 9094; Base de datos: bd_auth)

4. Endpoints

Registrar usuario

POST /auth/register

```json
{
    "nombre": "Caro Alumna",
    "username": "est1",
    "password": "1234",
    "role": "ESTUDIANTE",
    "curso": "2° Básico A"
}
```

role puede ser UTP, DOCENTE o ESTUDIANTE. El curso solo es obligatorio para ESTUDIANTE.

Respuesta (201):

```json
{
    "id": 3,
    "nombre": "Caro Alumna",
    "username": "est1",
    "role": "ESTUDIANTE",
    "curso": "2° Básico A"
}
```

Si el usuario ya existe responde 409; si el rol o los datos son inválidos responde 400.

Iniciar sesión

POST /auth/login

```json
{
    "username": "est1",
    "password": "1234"
}
```

Respuesta exitosa (200):

```json
{
    "token": "TOKEN_GENERADO",
    "id": 3,
    "nombre": "Caro Alumna",
    "username": "est1",
    "role": "ESTUDIANTE",
    "curso": "2° Básico A"
}
```

Respuesta fallida (401):

```json
{
    "status": 401,
    "error": "Unauthorized",
    "message": "Usuario o clave incorrectos"
}
```

Listar estudiantes (solo DOCENTE y UTP)

GET /auth/estudiantes?curso=2° Básico A

Respuesta:

```json
[
    { "id": 3, "nombre": "Caro Alumna", "username": "est1", "role": "ESTUDIANTE", "curso": "2° Básico A" }
]
```

5. Pruebas con Postman

Se recomienda realizar las pruebas en el siguiente orden:

Ejecutar el microservicio auth-service (y el api-gateway).
Enviar una petición POST a /auth/register.
Registrar un usuario UTP, un DOCENTE y un ESTUDIANTE.
Enviar una petición POST a /auth/login.
Ingresar las credenciales registradas.
Obtener el token de autenticación.

6. Integración con otros microservicios

El token generado por auth-service se utiliza para autenticar las solicitudes hacia el resto de los microservicios.

Para enviarlo mediante HTTP, utilizar el encabezado:

```http
Authorization: Bearer TU_TOKEN
```

El api-gateway valida el token, revisa el rol según la ruta y reenvía a cada servicio los encabezados X-User-Id, X-User-Role y X-User-Curso.
