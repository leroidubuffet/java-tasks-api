# Ejercicio: subagentes en Claude Code

---

## Repositorio del ejercicio

**Java:** `java-tasks-api` — https://github.com/leroidubuffet/java-tasks-api

---

## Qué vas a aprender

Un subagente es un agente con su propio contexto, sus propias herramientas y un objetivo único. Claude Code puede invocarlos automáticamente cuando le pides que coordine trabajo entre varios de ellos.

El ejercicio tiene dos partes:

1. **Instalación:** copiarás los tres subagentes en el lugar correcto y verificarás que Claude Code los reconoce.
2. **Ejecución en cascada:** pedirás al agente principal que coordine los tres subagentes para añadir un endpoint nuevo de extremo a extremo.

---

## Parte 1. Instalación

### Paso 1. Crear el directorio de subagentes

Los subagentes viven en `.claude/agents/` dentro del repositorio. Créalo si no existe:

```bash
mkdir -p .claude/agents
```

### Paso 2. Copiar los tres archivos de subagente

```bash
cp feature-implementer.md .claude/agents/
cp test-writer.md        .claude/agents/
cp documenter.md         .claude/agents/
```

Comprueba que están los tres:

```bash
ls .claude/agents/
# feature-implementer.md   test-writer.md   documenter.md
```

### Paso 3. Verificar que Claude Code los reconoce

Abre Claude Code en el repositorio:

```bash
claude
```

Pide la lista de subagentes disponibles:

```
/agents
```
La pantalla mostrará dos pestañas: `Running` y `Library`.

Deberías ver los tres: `feature-implementer`, `test-writer` y `documenter`. Si no aparecen, comprueba que los archivos tienen el bloque YAML con `name:` al principio y que están en `.claude/agents/`.

> **Para reflexionar:** Abre uno de los archivos `.md` y lee el frontmatter YAML. ¿Qué hace el campo `tools`? ¿Por qué `feature-implementer` no tiene `Bash` pero `test-writer` sí?

---

## Parte 2. Ejecución en cascada

### Paso 4. Lanzar el pipeline

Pide al agente principal que coordine los tres subagentes para añadir un endpoint nuevo:

```
Añade el endpoint GET /api/tasks/stats que devuelva el recuento de tareas
por estado (PENDING, IN_PROGRESS, DONE). Coordina a feature-implementer,
test-writer y documenter para implementarlo, testarlo y documentarlo.
```

El agente principal actuará como orquestador: llamará a `feature-implementer`, esperará su resultado, pasará ese resultado a `test-writer`, y finalmente pasará los cambios a `documenter`.

¿Qué pasaría si no le indicamos qué agentes usar? ¿Podemos decirle simplemente que use los agentes disponibles? Compruébalo.

### Paso 5. Seguir la traza de delegaciones

Observa la salida de Claude Code mientras trabaja. Verás dos fases diferenciadas:

- **feature-implementer** lee el controlador y el servicio existentes, añade el método `getStats()` al servicio, el método `findCountByStatus()` al repositorio si hace falta, y el endpoint `GET /api/tasks/stats` al controlador.
Las dos fases siguientes las hace en paralelo.
- **test-writer** lee el código que acaba de escribir el subagente anterior, escribe los tests de integración con `MockMvc` y los ejecuta con `mvn test`.
- **documenter** lee los archivos modificados y añade Javadoc a los métodos públicos nuevos.

Verás en el terminal una tabla como esta:

```bash
⏺ Los tres subagentes están en marcha. El estado actual:

  ┌─────────────────────┬──────────────────────────────────────────────────┬────────────┐
  │       Agente        │                      Tarea                       │   Estado   │
  ├─────────────────────┼──────────────────────────────────────────────────┼────────────┤
  │ feature-implementer │ TaskStats, countByStatus, getStats(), GET /stats │ Completado │
  ├─────────────────────┼──────────────────────────────────────────────────┼────────────┤
  │ test-writer         │ Tests unitarios + integración para /stats        │ En curso   │
  ├─────────────────────┼──────────────────────────────────────────────────┼────────────┤
  │ documenter          │ Javadoc en los 4 archivos nuevos/modificados     │ En curso   │
  └─────────────────────┴──────────────────────────────────────────────────┴────────────┘

  Cuando terminen los dos últimos te confirmo los resultados.
```

**Para reflexionar:** ¿Por qué el agente principal no implementa el código directamente? ¿Qué ventaja tiene separar el contexto de cada subagente?

### Paso 6. Revisar el resultado

Cuando el pipeline termine, revisa los cambios:

```bash
git diff
```

Comprueba tres cosas:

1. El endpoint existe en `TaskController` y sigue el patrón `ResponseEntity<ApiResponse<T>>`.
2. Hay tests nuevos en `src/test/` que verifican el recuento por estado.
3. Los métodos nuevos tienen Javadoc.

Verifica que la aplicación sigue funcionando con todos los tests pasando:

```bash
mvn test
```

> **Para reflexionar:** El ejercicio de `CLAUDE.md` tenía la regla "Los controllers nunca llaman directamente a métodos del repository." ¿La ha respetado `feature-implementer`? ¿Dónde vive la lógica de agrupación por estado, en el controller o en el service?

---

## Parte 3. Experimentación (opcional)

### Paso 7. Rechazar y reintentar

Revierte los cambios del step 4:

```bash
git checkout .
```

Vuelve a lanzar el mismo prompt, pero esta vez interrumpe después de que `feature-implementer` termine y antes de que `test-writer` empiece. Pide al agente principal que descarte el trabajo del implementador y reintente con una instrucción más específica:

```
El endpoint está bien pero el DTO de respuesta debería llamarse TaskStats
y tener los campos pending, inProgress y done (no un Map genérico).
Pide a feature-implementer que rehaga la implementación con ese DTO.
```

> **Para reflexionar:** ¿Qué diferencia hay entre rechazar la tarea de un subagente y pedirle que la rehaga? ¿El orquestador tiene que saber cómo implementar el DTO para coordinar el reintento?

---

## Conclusión

El ejercicio demuestra tres cosas:

1. **Instalación:** los subagentes son archivos Markdown con frontmatter YAML. No requieren configuración adicional. El campo `tools` limita lo que cada subagente puede hacer, lo que reduce el riesgo de que actúe fuera de su alcance.

2. **Cascada:** el agente principal coordina los tres subagentes en orden, pasando el resultado de cada uno al siguiente. Cada subagente trabaja en un contexto separado: `feature-implementer` no sabe que luego vendrá `test-writer`, y `test-writer` no sabe que `documenter` añadirá Javadoc después.

3. **Control:** el orquestador puede rechazar el trabajo de un subagente y pedir un reintento sin conocer los detalles de implementación. Separa la coordinación de la ejecución.

**La pregunta de fondo:** ¿Qué tareas de tu proyecto cotidiano podrían beneficiarse de este patrón? Piensa en tareas con tres o más pasos donde cada paso tiene criterios de éxito verificables.
