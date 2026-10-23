# Proyecto Desarrollo Web Servidor

## Arranque

Para iniciar la aplicación:

```bash
mvn spring-boot:run
```

La aplicación se ejecuta en el puerto `8080`.

## Rutas iniciales

* `/hola`
* `/estado`
* `/cosas-de-hacer/resumen`

## Paso 7 · Defectos conocidos de la API

| Prueba                                      | Resultado actual           | Resultado correcto           | Se arregla en     |
| ------------------------------------------- | -------------------------- | ---------------------------- | ----------------- |
| `GET /tareas/999`                           | `200` con cuerpo vacío     | `404 Not Found`              | UD2               |
| `POST /tareas` con `{}`                     | Crea una tarea sin título  | `400` explicando qué falta   | UD3               |
| `POST /tareas` correcto                     | Responde `200 OK`          | `201 Created`                | UD2               |
| `POST /tareas` con `"completada": "quizás"` | `400` sin explicación útil | Error legible                | UD3               |
| Reiniciar la aplicación                     | Se pierden los datos       | Los datos siguen disponibles | UD5               |
| Campo interno del modelo                    | Se publica en la respuesta | Solo se publica lo decidido  | UD3, mediante DTO |

## Paso 8 · Revisión

### Decisiones

* **Identificador:** el `id` lo genera el servidor para controlar la asignación de identificadores y evitar que el cliente tenga que proporcionarlo.
* **Filtros:** los filtros se colocan en la query string porque modifican la consulta sobre una colección sin cambiar la ruta del recurso.

### Defectos conocidos

1. Las tareas que no existen todavía no devuelven `404`.
2. Actualmente se puede crear una tarea sin validar que tenga los campos necesarios.
3. El `POST` devuelve `200 OK` en lugar de `201 Created`.
4. Los errores de formato o tipo todavía no tienen mensajes preparados para el usuario.
5. Los datos se almacenan en memoria y se pierden al reiniciar la aplicación.
6. El modelo se utiliza directamente en las respuestas, por lo que sus campos pueden quedar expuestos.

### Pregunta pendiente

> ¿Por qué Spring puede convertir automáticamente el JSON enviado desde Postman en un objeto `Tarea` cuando utilizamos `@RequestBody`?

## Comprobaciones adicionales

* Un filtro que no encuentra coincidencias devuelve un array vacío `[]`.
* Al quitar el filtro se recuperan todos los registros existentes.
* Las operaciones de las dos entidades se comprueban mediante las peticiones de la práctica.


## 1. Campos que quedan fuera de cada DTO

| DTO | Fuera | Por qué |
|-----|-------|---------|
| `ProyectoRequest` / `TareaRequest` (alta y sustitución) | `id` | Lo asigna el servidor; si lo aceptara, el cliente podría pisar el de otro recurso. |
| `TareaRequest`, `TareaPatchRequest` | `notaInterna` | Es de uso interno y el cliente no debe poder escribirla. |
| `TareaResponse` | `notaInterna` | Dato interno: no forma parte del contrato. |
| `ProyectoPatchRequest` | `fechaInicio`, `fechaFin` | La regla de coherencia compara las dos juntas y un PATCH solo trae una parte; las fechas se cambian con PUT. |

`ProyectoRequest` y `ProyectoResponse` incluyen `fechaInicio` y `fechaFin`: se validan, se guardan y se devuelven
(antes se descartaban). Los DTO de entrada recortan espacios de `nombre` y `titulo` **antes** de validar, para que
`"  ab  "` no pase `@Size` y se guarde como `"ab"`.

## 2. Anotaciones de validación propias

- `@FechasCoherentes` (a nivel de clase, en `ProyectoRequest`): exige `fechaFin` ≥ `fechaInicio`. `@Pattern`
  no puede resolverlo porque trabaja sobre **un solo campo** y como texto; aquí hay que comparar dos campos
  entre sí. El error se asocia a `fechaFin` para que el cliente sepa qué corregir.
- `@PrioridadValida` (en `TareaRequest` y `TareaPatchRequest`): admite `baja`, `media` o `alta`. Aquí sí
  habría valido `@Pattern("baja|media|alta")`; la anotación propia se justifica porque la lista vive en un
  único sitio, el mensaje es el mismo en POST, PUT y PATCH, y mañana la lista puede salir de un catálogo
  (UD5) sin tocar los DTO.

## 3. Formato de error

Todos los errores usan `ProblemDetail` (RFC 9457, `application/problem+json`) con `title`, `status`,
`detail` en español e `instance` (la ruta pedida), más `momento`; los de validación añaden `invalidParams`
con `campo` y `motivo`. El único sitio que construye errores es `ManejadorDeErrores`, que además cubre los
que lanza Spring antes de llegar a un controlador (405, 415, ruta inexistente, id no numérico). El 500
responde un texto fijo y deja el detalle solo en el log.

**Campo que añadiría para depurar:** un identificador de error (`idError`, un UUID) que aparezca en la
respuesta **y** en la línea del log. Hoy el 500 no cuenta nada hacia fuera (correcto), pero eso significa que
si un cliente reporta un fallo no hay forma de enlazar su respuesta con el stacktrace. Con el id, el cliente
copia un valor y tú lo buscas en el log sin filtrar por hora.

## 4. El caso que responde 409

Crear o renombrar un proyecto con un nombre que ya existe (sin distinguir mayúsculas ni espacios en los
extremos): `POST`, `PUT` y `PATCH /proyectos`. No es un **400** porque la petición está bien formada y cumple
todas las reglas de validación: lo mismo con otro nombre funcionaría, el problema es el estado actual del
servidor. No es un **404** porque el recurso al que se dirige la petición existe (o es la colección). Un 409
dice exactamente eso: «tu petición es válida, pero choca con lo que ya hay». Conservar el propio nombre en un
PUT no es conflicto.

## 5. Por qué la API se queda en el nivel 2

Ver el criterio 10 de `auditoria-rest.md`: la hipermedia añade complejidad al servidor que muy pocos clientes
aprovechan; con un único cliente que conoce las rutas, el coste supera al beneficio. Es una decisión de coste
y beneficio tomada a conciencia.

## Limitaciones conocidas (el backend sigue en memoria hasta la UD5)

- Todo se pierde al reiniciar; no hay persistencia hasta PostgreSQL (UD5).
- Los controladores guardan los datos, generan ids, filtran y aplican reglas: se separa en capas con un
  `service` en la UD4.
- No se comprueba que el `proyectoId` de una tarea exista, y borrar un proyecto no toca sus tareas: la regla
  «el proyecto debe existir» aún no tiene sitio (UD4).
- Las pruebas automáticas existentes usan MockMvc; aún no hay tests de la lógica sin servidor (UD4).