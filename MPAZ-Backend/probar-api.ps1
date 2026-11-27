# probar-api.ps1
# Prueba completa del backend MPAZ a traves del gateway (http://localhost:9090).
#
# Uso (con el backend ya levantado, desde la carpeta que tiene el docker-compose.yml):
#     .\probar-api.ps1
# Si PowerShell bloquea el script:
#     powershell -ExecutionPolicy Bypass -File .\probar-api.ps1
#
# Se puede ejecutar varias veces: los usuarios se reutilizan y cada corrida
# crea una unidad y una asignacion nuevas.

$ErrorActionPreference = "Stop"
$base = "http://localhost:9090"

# ---------- Funciones de ayuda ----------

function Api {
    param([string]$Metodo, [string]$Ruta, $Cuerpo = $null, [string]$Token = "")
    $p = @{ Method = $Metodo; Uri = "$base$Ruta" }
    if ($Token -ne "") { $p.Headers = @{ Authorization = "Bearer $Token" } }
    if ($null -ne $Cuerpo) {
        $json = ConvertTo-Json -InputObject $Cuerpo -Depth 5
        $p.Body = [System.Text.Encoding]::UTF8.GetBytes($json)
        $p.ContentType = "application/json; charset=utf-8"
    }
    Invoke-RestMethod @p
}

function CodigoHttp {
    param([string]$Metodo, [string]$Ruta, $Cuerpo = $null, [string]$Token = "")
    try { Api $Metodo $Ruta $Cuerpo $Token | Out-Null; return 200 }
    catch { return [int]$_.Exception.Response.StatusCode }
}

function Paso($texto) {
    Write-Host ""
    Write-Host "== $texto" -ForegroundColor Cyan
}

function Registrar($datos) {
    try {
        Api POST "/auth/register" $datos | Out-Null
        Write-Host "Creado: $($datos.username) ($($datos.role))"
    }
    catch {
        if ([int]$_.Exception.Response.StatusCode -eq 409) {
            Write-Host "Ya existia: $($datos.username)"
        }
        else { throw }
    }
}

# ---------- 0. Gateway ----------

Paso "0. Comprobando que el gateway responde"
try { Invoke-WebRequest -Uri "$base/unidades" -Method GET -UseBasicParsing | Out-Null }
catch {
    if ($null -eq $_.Exception.Response) {
        Write-Host "No responde $base. Levanta el backend con: docker compose up -d --build" -ForegroundColor Red
        exit 1
    }
}
Write-Host "Gateway OK"

# ---------- 1. Usuarios y tokens ----------

Paso "1. Registrar los tres usuarios"
Registrar @{ nombre = "Ana UTP";      username = "utp1"; password = "1234"; role = "UTP" }
Registrar @{ nombre = "Beto Docente"; username = "doc1"; password = "1234"; role = "DOCENTE" }
Registrar @{ nombre = "Caro Alumna";  username = "est1"; password = "1234"; role = "ESTUDIANTE"; curso = "2do Basico A" }

Paso "2. Login (el token lo entrega el login, no el registro)"
$utp = Api POST "/auth/login" @{ username = "utp1"; password = "1234" }
$doc = Api POST "/auth/login" @{ username = "doc1"; password = "1234" }
$est = Api POST "/auth/login" @{ username = "est1"; password = "1234" }
Write-Host "UTP: id $($utp.id) | DOCENTE: id $($doc.id) | ESTUDIANTE: id $($est.id), curso $($est.curso)"

# ---------- 2. UTP crea la unidad ----------

Paso "3. UTP crea una unidad (queda en BORRADOR)"
$unidad = Api POST "/unidades" @{
    asignatura  = "Lenguaje"
    titulo      = "Los cuentos"
    codigoOA    = "OA 3"
    objetivo    = "Leer cuentos breves"
    explicacion = "Un cuento tiene inicio, desarrollo y final."
    ejemplo     = "Caperucita Roja"
} $utp.token
Write-Host "Unidad id $($unidad.id), estado $($unidad.estado)"

Paso "4. UTP crea un desafio y una pregunta de la prueba final"
$desafio = Api POST "/unidades/$($unidad.id)/preguntas" @{
    texto        = "Quien entra al bosque?"
    alternativaA = "El lobo"
    alternativaB = "Caperucita"
    alternativaC = "El cazador"
    alternativaD = "La abuela"
    correcta     = 1
    esPruebaFinal = $false
} $utp.token
$final = Api POST "/unidades/$($unidad.id)/preguntas" @{
    texto        = "Que parte del cuento presenta a los personajes?"
    alternativaA = "El inicio"
    alternativaB = "El desarrollo"
    alternativaC = "El final"
    alternativaD = "El titulo"
    correcta     = 0
    esPruebaFinal = $true
} $utp.token
Write-Host "Desafio id $($desafio.id) | Pregunta de prueba final id $($final.id)"

Paso "5. UTP envia a revision y autoriza"
$u = Api PATCH "/unidades/$($unidad.id)/estado" @{ estado = "EN_REVISION" } $utp.token
Write-Host "Estado: $($u.estado)"
$u = Api PATCH "/unidades/$($unidad.id)/estado" @{ estado = "AUTORIZADA" } $utp.token
Write-Host "Estado: $($u.estado)"

# ---------- 3. Docente asigna ----------

Paso "6. Docente asigna la unidad al curso"
$ini = [DateTimeOffset]::UtcNow.AddDays(-1).ToUnixTimeMilliseconds()
$fin = [DateTimeOffset]::UtcNow.AddDays(365).ToUnixTimeMilliseconds()
$asig = Api POST "/asignaciones" @{
    unidadId              = $unidad.id
    curso                 = "2do Basico A"
    fechaInicio           = $ini
    fechaFin              = $fin
    maxIntentos           = 3
    notificarCurso        = $true
    permitirVerRespuestas = $true
} $doc.token
Write-Host "Asignacion id $($asig.id), curso $($asig.curso), vigente: $($asig.vigente)"

# ---------- 4. Estudiante ----------

Paso "7. Estudiante ve sus asignaciones"
$mias = Api GET "/asignaciones/mias" $null $est.token
Write-Host "Asignaciones del estudiante: $(@($mias).Count)"

Paso "8. Estudiante resuelve el desafio (elegida 1 = alternativa B)"
$r = Api POST "/desafios/$($desafio.id)/resolver" @{ elegida = 1 } $est.token
$r | ConvertTo-Json -Depth 5

Paso "9. Estudiante entrega la prueba final"
$intento = Api POST "/pruebas/$($asig.id)/intentos" @{
    respuestas = @( @{ preguntaId = $final.id; elegida = 0 } )
} $est.token
$intento | ConvertTo-Json -Depth 5

Paso "10. Estudiante ve sus intentos"
Api GET "/pruebas/$($asig.id)/mis-intentos" $null $est.token | ConvertTo-Json -Depth 5

# ---------- 5. Docente y UTP revisan ----------

Paso "11. Docente revisa informes y rendimiento"
Write-Host "-- Informe de la asignacion"
Api GET "/informes/asignacion/$($asig.id)" $null $doc.token | ConvertTo-Json -Depth 5
Write-Host "-- Informe del estudiante en la unidad"
Api GET "/informes/estudiante/$($est.id)/unidad/$($unidad.id)" $null $doc.token | ConvertTo-Json -Depth 5
Write-Host "-- Rendimiento por OA"
Api GET "/rendimiento" $null $doc.token | ConvertTo-Json -Depth 5
Write-Host "-- Rendimiento por curso"
Api GET "/rendimiento/cursos" $null $utp.token | ConvertTo-Json -Depth 5

Paso "12. Docente deja una observacion"
Api POST "/observaciones" @{
    estudianteId = $est.id
    unidadId     = $unidad.id
    texto        = "Buen trabajo en la unidad."
} $doc.token | ConvertTo-Json -Depth 5

# ---------- 6. Seguridad ----------

Paso "13. Demostracion de seguridad (codigo obtenido / esperado)"
$c1 = CodigoHttp GET "/unidades"
$c2 = CodigoHttp GET "/unidades" $null $utp.token
$c3 = CodigoHttp POST "/unidades" @{ asignatura = "x" } $est.token
$c4 = CodigoHttp GET "/internal/unidades" $null $utp.token
Write-Host "GET /unidades sin token ........................ $c1 (esperado 401)"
Write-Host "GET /unidades con token UTP .................... $c2 (esperado 200)"
Write-Host "POST /unidades con token de estudiante ......... $c3 (esperado 403)"
Write-Host "GET /internal/unidades (ruta bloqueada) ........ $c4 (esperado 403)"

Write-Host ""
Write-Host "Listo. Si todos los pasos terminaron sin error rojo, el backend funciona." -ForegroundColor Green
