# Ejercicio: orquestación paralela con Antigravity

---

## Repositorio del ejercicio

**Java:** `java-tasks-api` — https://github.com/leroidubuffet/java-tasks-api

> **Nota si ya hiciste el ejercicio de subagentes:** ese ejercicio implementa el mismo endpoint `GET /api/tasks/stats` que se pide en la Tarea A de este ejercicio. Para empezar con el código limpio, ejecuta:
> ```bash
> git checkout src/
> ```

---

## Qué vas a aprender

Antigravity tiene dos vistas: Editor o Antigravity IDE (escribir y revisar código) y Manager o Antigravity 2.0 (supervisar agentes que trabajan en paralelo). Este ejercicio usa la vista Manager / Antigravity 2.0 para lanzar tres tareas independientes sobre la tasks API al mismo tiempo, revisar los resultados y gestionar un rechazo con reintento.

Al terminar entenderás la diferencia entre supervisar agentes y escribir código tú mismo, y habrás visto en acción el patrón paralelo aplicado al desarrollo de software.

---

## Parte 1. Preparación

### Paso 1. Abrir Antigravity

### Paso 2. empezar una conversación nueva

Ve a Proyectos y pulsa en el ícono del directorio. De las dos opciones elige "Quick start" o "Inicio rápido".
Pide a Antigravity que descargue el repositorio https://github.com/leroidubuffet/java-tasks-api
Creará un proyecto y te pedirá permisos para descargar el respositorio. Revisa sus peticiones y sus indicaciones.

---

## Parte 2. Lanzar las tres tareas en paralelo

### Paso 4. Crear las tareas

Añade las tres tareas siguientes en conversaciones separadas. Cada una va en su propia conversación y se ejecuta de forma independiente:

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

Las tres conversaciones se ejecutarán en paralelo. Cada conversación muestra qué está haciendo el agente en cada momento.

Probablemente sugerirán un plan de implementación. Podemos revisarlo y añadir comentarios seleccionando párrafos. Tenemos que leerlo entero y consultar con Antigravity todo lo necesario. Una vez lo hayamos revisado por completo, pulsamos "Proceder/Proceed".

El plan de implementación y los cambios de cada archivo están en el panel derecho. Podemos añadir comentarios a ambas cosas si es necesario. El agente seguirá su curso si no hacemos nada y nos irá avisando cuando necesite neustra participación.

---

## Parte 3. Revisar y gestionar los resultados

### Paso 6. Revisar las ejecuciones finales

Al terminar a veces nos elabora un Walkthrough o Guía paso a paso que nos explica lo que ha hecho.

¿Cómo han trabajado los agentes? ¿Qué te han pedido? ¿Qué te parece esta forma de trabajo?

---

## Parte 4. Experimentación (opcional)

### Paso 11. Forzar un conflicto · *Nivel: intermedio · ~20 min*

Revierte los cambios y lanza de nuevo las tres tareas, pero esta vez con una variante: en la tarea A y en la tarea C, ambas incluyen modificar `TaskController.java`. Observa qué ocurre cuando dos agentes intentan editar el mismo archivo en paralelo.

> **Para reflexionar:** ¿Cómo gestiona Antigravity el conflicto? ¿Qué estrategia usarías para evitar este problema al diseñar tareas paralelas?

---

## Cierre

**La pregunta de fondo:** ¿Qué diferencia hay entre este ejercicio y el de subagentes en Claude Code? En Antigravity tú eres el orquestador: decides qué tareas lanzar, cuándo aceptar y cómo refinar. En Claude Code el orquestador es el propio modelo. Esa diferencia de control es exactamente lo que separa un entorno interactivo de un pipeline automatizado.

