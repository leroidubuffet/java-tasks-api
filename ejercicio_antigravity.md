# Ejercicio: orquestación paralela con Antigravity Manager

---

## Repositorio del ejercicio

**Java:** `java-tasks-api` — https://github.com/leroidubuffet/java-tasks-api

Si no lo tienes clonado:

```bash
git clone https://github.com/leroidubuffet/java-tasks-api
cd java-tasks-api
```

> **Nota si ya hiciste el ejercicio de subagentes:** ese ejercicio implementa el mismo endpoint `GET /api/tasks/stats` que se pide en la Tarea A de este ejercicio. Para empezar con el código limpio, ejecuta:
> ```bash
> git checkout src/
> ```

---

## Qué vas a aprender

Antigravity tiene dos vistas: Editor o Antigravity IDE (escribir y revisar código) y Manager o Antigravity 2.0 (supervisar agentes que trabajan en paralelo). Este ejercicio usa la vista Manager para lanzar tres tareas independientes sobre la tasks API al mismo tiempo, revisar los resultados y gestionar un rechazo con reintento.

Al terminar entenderás la diferencia entre supervisar agentes y escribir código tú mismo, y habrás visto en acción el patrón paralelo aplicado al desarrollo de software.

---

## Parte 1. Preparación

### Paso 1. Abrir Antigravity

Abre Antigravity en la MV. Pulsa "Agent Manager" Cuando aparezcan los cuadros de diálogo iniciales, pulsa **Skip** en todos.

### Paso 2. Abrir el repositorio

En Antigravity, abre la carpeta `java-tasks-api`. Comprueba que el proyecto se carga correctamente y que puedes ver la estructura `src/main/java/com/curso/tasks/`.

### Paso 3. Cambiar entre vista Manager y Editor

Desde vista Manager pulsa el botón **Open Editor** en la barra superior (o usa el atajo **Cmd + E** para alternar entre Editor y Manager).
Desde vista Editor pulsa el botón **Open Agent Manager** en la barra superior (o usa el atajo **Cmd + E** para alternar entre Editor y Manager).

La vista Manager muestra un panel vacío con el botón para añadir tareas. Aquí es donde vas a orquestar el trabajo paralelo.

---

## Parte 2. Lanzar las tres tareas en paralelo

### Paso 4. Crear las tareas

En el panel Manager, añade las tres tareas siguientes en conversaciones separadas. Cada una va en su propia conversación y se ejecuta de forma independiente:

**Tarea A**
```
Añade el endpoint GET /api/tasks/stats que devuelva el recuento de tareas
por estado. La respuesta debe seguir el patrón ApiResponse<T> del proyecto
y la lógica debe vivir en TaskService, no en el controller.
```

**Tarea B**
```
Añade el endpoint GET /api/tasks/{id}/history que devuelva el historial
de cambios de estado de una tarea. Decide el modelo de datos que necesitas
para almacenar el historial.
```

**Tarea C**
```
Añade paginación al endpoint GET /api/tasks. Usa los parámetros page y size.
Cuando no se pasan, el comportamiento actual (devolver todas las tareas)
debe mantenerse para no romper los tests existentes.
```

### Paso 5. Observar la ejecución

Verás tres conversaciones en el Manager. Cada conversación muestra qué está haciendo el agente en cada momento.

Podemos ver los archivos modificados por los subagentes, ver el diff y añadir comentarios para que hagan cambios.

> **Para reflexionar:** ¿Qué ventaja tiene que el desarrollador pueda seguir trabajando mientras los agentes ejecutan? ¿En qué situaciones esto sería especialmente valioso?

---

## Parte 3. Revisar y gestionar los resultados

### Paso 6. Revisar los diffs

Cuando las tres tareas terminan, revisa el diff de cada una en su conversación. Antes de aceptar o rechazar, comprueba para cada tarea:

- ¿Los tests existentes siguen pasando? Puedes lanzarlos con `mvn clean test` en la terminal, aunque los subagentes probablemente hayan lanzado los tests por su cuenta.

### Paso 7. Aceptar A y C, rechazar B

**Acepta** la tarea A (stats) y la tarea C (paginación) si el código es correcto.

**Rechaza** la tarea B (history). El problema típico es que el agente inventa un modelo de datos para el historial (una nueva entidad `TaskHistory`, una tabla adicional, etc.) que añade complejidad no acordada. Si tienes que rechazarla, incluye instrucciones específicas para el reintento:

```
El historial no necesita persistencia propia. Implementa el endpoint
devolviendo únicamente el estado actual y la fecha de creación de la tarea
(campos que ya existen en Task). El endpoint GET /api/tasks/{id}/history
debe devolver List<TaskEvent> donde TaskEvent es un record con dos campos:
status (String) y timestamp (LocalDateTime).
```

### Paso 8. Verificar el reintento

El agente relanza la tarea B con las instrucciones nuevas. Cuando termine, revisa el diff de nuevo. Comprueba que ahora usa `Task.status` y `Task.createdAt` en lugar de una entidad nueva.

Acepta la tarea B si el resultado es correcto.

### Paso 9. Ejecutar los tests

Abre un terminal y ejecuta:

```bash
mvn test
```

Los tests existentes deben pasar. Si la tarea C (paginación) añadió el parámetro `page` y `size` como requeridos en lugar de opcionales, los tests de integración existentes fallarán. En ese caso, pide al agente que corrija el endpoint para que los parámetros sean opcionales.

> **Para reflexionar:** ¿El agente de la tarea B habría producido un resultado mejor si las instrucciones iniciales hubieran sido más concretas? ¿Qué información faltaba en el prompt original?

---

## Parte 4. Experimentación (opcional)

### Paso 10. Comparar Manager con Editor · *Nivel: básico · ~10 min*

Revierte los cambios:

```bash
git checkout src/
```

Ahora intenta hacer la misma tarea A (stats) desde la vista Editor, con una conversación directa con el agente. Compara el proceso con el del Manager.

> **Para reflexionar:** ¿Cuándo prefieres el Editor y cuándo el Manager? La regla práctica: tareas con verificación independiente que no tocan los mismos archivos van al Manager; tareas que requieren conversación cercana o tienen dependencias fuertes entre sí van al Editor.

### Paso 11. Forzar un conflicto · *Nivel: intermedio · ~20 min*

Revierte los cambios y lanza de nuevo las tres tareas, pero esta vez con una variante: en la tarea A y en la tarea C, ambas incluyen modificar `TaskController.java`. Observa qué ocurre cuando dos agentes intentan editar el mismo archivo en paralelo.

> **Para reflexionar:** ¿Cómo gestiona Antigravity el conflicto? ¿Qué estrategia usarías para evitar este problema al diseñar tareas paralelas?

---

## Conclusión

El ejercicio demuestra tres cosas:

1. **Paralelismo sin código:** el Manager lanza y coordina agentes en paralelo sin que escribas ningún orquestador. El modelo decide cuándo las tareas son independientes y las ejecuta simultáneamente.

2. **El desarrollador como supervisor:** mientras los agentes trabajan, el desarrollador no está bloqueado. Su rol cambia de ejecutor a revisor: evalúa diffs, acepta o rechaza, y proporciona instrucciones más precisas cuando el resultado no encaja.

3. **La precisión del prompt importa:** la tarea B falló no porque el agente sea incapaz, sino porque las instrucciones dejaban abierta una decisión de diseño que el agente resolvió de forma genérica. Instrucciones más concretas producen resultados más predecibles.

**La pregunta de fondo:** ¿Qué diferencia hay entre este ejercicio y el de subagentes en Claude Code? En Antigravity tú eres el orquestador: decides qué tareas lanzar, cuándo aceptar y cómo refinar. En Claude Code el orquestador es el propio modelo. Esa diferencia de control es exactamente lo que separa un entorno interactivo de un pipeline automatizado.
