# Proyecto Base Implementando Clean Architecture

## Antes de Iniciar

Empezaremos por explicar los diferentes componentes del proyectos y partiremos de los componentes externos, continuando con los componentes core de negocio (dominio) y por último el inicio y configuración de la aplicación.

Lee el artículo [Clean Architecture — Aislando los detalles](https://medium.com/bancolombia-tech/clean-architecture-aislando-los-detalles-4f9530f35d7a)

# Arquitectura

![Clean Architecture](https://miro.medium.com/max/1400/1*ZdlHz8B0-qu9Y-QO3AXR_w.png)

## Domain

Es el módulo más interno de la arquitectura, pertenece a la capa del dominio y encapsula la lógica y reglas del negocio mediante modelos y entidades del dominio.

## Usecases

Este módulo gradle perteneciente a la capa del dominio, implementa los casos de uso del sistema, define lógica de aplicación y reacciona a las invocaciones desde el módulo de entry points, orquestando los flujos hacia el módulo de entities.

## Infrastructure

### Helpers

En el apartado de helpers tendremos utilidades generales para los Driven Adapters y Entry Points.

Estas utilidades no están arraigadas a objetos concretos, se realiza el uso de generics para modelar comportamientos
genéricos de los diferentes objetos de persistencia que puedan existir, este tipo de implementaciones se realizan
basadas en el patrón de diseño [Unit of Work y Repository](https://medium.com/@krzychukosobudzki/repository-design-pattern-bc490b256006)

Estas clases no puede existir solas y debe heredarse su compartimiento en los **Driven Adapters**

### Driven Adapters

Los driven adapter representan implementaciones externas a nuestro sistema, como lo son conexiones a servicios rest,
soap, bases de datos, lectura de archivos planos, y en concreto cualquier origen y fuente de datos con la que debamos
interactuar.

### Entry Points

Los entry points representan los puntos de entrada de la aplicación o el inicio de los flujos de negocio.

## Application

Este módulo es el más externo de la arquitectura, es el encargado de ensamblar los distintos módulos, resolver las dependencias y crear los beans de los casos de use (UseCases) de forma automática, inyectando en éstos instancias concretas de las dependencias declaradas. Además inicia la aplicación (es el único módulo del proyecto donde encontraremos la función “public static void main(String[] args)”.

**Los beans de los casos de uso se disponibilizan automaticamente gracias a un '@ComponentScan' ubicado en esta capa.**

## Historia 1: registrar usuarios

El endpoint `POST /api/v1/usuarios` registra un usuario y responde `201 Created`. El cuerpo usa nombres de campos en `snake_case`:

```json
{
  "nombre": "Ana",
  "apellido": "Pérez",
  "fecha_nacimiento": "1998-06-15",
  "direccion": "Calle 1",
  "telefono": "3001234567",
  "correo_electronico": "ana@example.com",
  "salario_base": 2500000
}
```

`nombre`, `apellido`, `correo_electronico` y `salario_base` son obligatorios. Se valida el formato del correo, que no esté registrado y que el salario esté entre 0 y 15.000.000. Los errores de entrada responden `400 Bad Request`; un correo duplicado responde `409 Conflict`. La respuesta exitosa incluye `id_usuario` y los datos del usuario.

El flujo mantiene la arquitectura existente: el modelo y el puerto están en `domain/model`, las reglas en `domain/usecase`, el mapeo JPA en `infrastructure/driven-adapters/jpa-repository` y el endpoint y sus errores en `infrastructure/entry-points/api-rest`. El servicio usa Spring MVC y JPA; no se migró a WebFlux/R2DBC.

Pasos para seguir el flujo en el código:

1. Define `User` y el contrato `UserRepository` en el dominio, sin dependencias de Spring o JPA.
2. Implementa en `UserUseCase` las validaciones del correo, campos requeridos, rango salarial y duplicados.
3. Implementa el puerto con `UsuarioEntity`, `UsuarioJpaRepository` y `UsuarioRepositoryAdapter`; la restricción única de correo protege también frente a registros concurrentes.
4. Expón el caso de uso en `UsuarioController`, valida el DTO y centraliza las respuestas de error en `GlobalExceptionHandler`.
5. Ejecuta las pruebas unitarias y prueba el endpoint con el ejemplo de arriba.

La persistencia usa PostgreSQL. La aplicación se conecta por defecto a `jdbc:postgresql://localhost:5432/crediya_db` y obtiene la contraseña de la variable de entorno `POSTGRES_PASSWORD`; no se debe guardar la contraseña en el repositorio. Para iniciar la base con Docker Compose desde PowerShell:

```powershell
$env:POSTGRES_PASSWORD = "<tu contraseña local>"
docker compose -f deployment/docker-compose.yml up -d
```

Mantén `POSTGRES_PASSWORD` definida en la misma terminal al iniciar la aplicación:

```powershell
$env:POSTGRES_PASSWORD = "<la misma contraseña local>"
.\gradlew.bat :app-service:bootRun
```

Para ejecutar las pruebas de los módulos:

```powershell
.\gradlew.bat :model:test :usecase:test :jpa-repository:test :api-rest:test :app-service:test `
  -x :model:pitest -x :usecase:pitest -x :jpa-repository:pitest -x :api-rest:pitest -x :app-service:pitest
```
