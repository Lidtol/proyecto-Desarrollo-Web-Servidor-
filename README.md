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
