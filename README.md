# java-tasks-api

API REST de gestion de tareas escrita en Java con Spring Boot. Permite crear, consultar, actualizar y eliminar tareas, con persistencia en base de datos H2 en memoria.

## Que contiene

```
src/main/java/com/curso/tasks/
  controller/TaskController.java   # endpoints REST
  service/TaskService.java         # logica de negocio
  repository/TaskRepository.java   # acceso a datos
  model/Task.java                  # entidad con estados PENDING / IN_PROGRESS / DONE
  model/ApiResponse.java           # wrapper de respuesta para todos los endpoints
src/test/                          # tests unitarios e integracion con MockMvc
```

## Arrancar la aplicacion

```bash
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080/api/tasks`.
La consola H2 esta en `http://localhost:8080/h2-console`.

## Endpoints

| Metodo | Ruta                  | Descripcion                        |
|--------|-----------------------|------------------------------------|
| GET    | `/api/tasks`          | Lista todas las tareas             |
| GET    | `/api/tasks?status=`  | Filtra por estado                  |
| GET    | `/api/tasks/{id}`     | Obtiene una tarea por id           |
| POST   | `/api/tasks`          | Crea una tarea nueva               |
| PUT    | `/api/tasks/{id}`     | Actualiza una tarea existente      |
| DELETE | `/api/tasks/{id}`     | Elimina una tarea                  |

## Tests

```bash
mvn test
```

---

Este repositorio es material de ejercicio del curso **IA generativa en el desarrollo de software**.

Los archivos [`ejercicio_claude_md.md`](./ejercicio_claude_md.md), [`ejercicio_subagents.md`](./ejercicio_subagents.md) y [`ejercicio_antigravity.md`](./ejercicio_antigravity.md) tienen instrucciones para los ejercicios.
