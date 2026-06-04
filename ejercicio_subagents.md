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

El repositorio incluye los tres subagentes en la carpeta `agents/`. Cópialos al directorio que Claude Code lee:

```bash
cp agents/feature-implementer.md .claude/agents/
cp agents/test-writer.md         .claude/agents/
cp agents/documenter.md          .claude/agents/
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

Observa la salida de Claude Code mientras trabaja. Verás las fases diferenciadas:

- **feature-implementer** lee el controlador y el servicio existentes, añade el método `getStats()` al servicio, el método `findCountByStatus()` al repositorio si hace falta, y el endpoint `GET /api/tasks/stats` al controlador.
- Las dos fases siguientes las puede ejecutar en paralelo.
- **test-writer** lee el código que acaba de escribir el subagente anterior, escribe los tests de integración con `MockMvc` y los ejecuta con `mvn test`.
- **documenter** lee los archivos modificados y añade Javadoc a los métodos públicos nuevos.

> **Para reflexionar:** ¿Por qué el agente principal no implementa el código directamente? ¿Qué ventaja tiene separar el contexto de cada subagente?

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

> **Para reflexionar:** El archivo `CLAUDE.md` del proyecto tiene la regla "Los controllers nunca llaman directamente a métodos del repository." ¿La ha respetado `feature-implementer`? ¿Dónde vive la lógica de agrupación por estado, en el controller o en el service?

---

## Parte 3. Experimentación (opcional)

Los tres pasos siguientes son independientes entre sí. Elige según el tiempo disponible. Los niveles de dificultad indican el esfuerzo esperado, no el conocimiento previo necesario.

---

### Paso 7. Rechazar y reintentar · *Nivel: básico · ~15 min*

Revierte los cambios del paso 4:

```bash
git checkout src/
```

Vuelve a lanzar el mismo prompt, pero esta vez interrumpe después de que `feature-implementer` termine y antes de que `test-writer` empiece. Pide al agente principal que descarte el trabajo del implementador y reintente con una instrucción más específica:

```
El endpoint está bien pero el DTO de respuesta debería llamarse TaskStats
y tener los campos pending, inProgress y done en lugar de un Map genérico.
Pide a feature-implementer que rehaga la implementación con ese DTO.
```

> **Para reflexionar:** ¿Qué diferencia hay entre rechazar la tarea de un subagente y pedirle que la rehaga? ¿El orquestador tiene que saber cómo implementar el DTO para coordinar el reintento?

---

### Paso 8. Diagnosticar un subagente roto · *Nivel: intermedio · ~20 min*

Este paso simula lo que ocurre cuando un subagente tiene instrucciones incompletas. Vas a introducir un fallo deliberado en `feature-implementer`, ejecutar el pipeline y diagnosticar qué salió mal.

#### 8a. Introducir el fallo

Abre `.claude/agents/feature-implementer.md` y elimina el bloque completo de instrucciones sobre el patrón de respuesta del controller:

```
- Always return `ResponseEntity<ApiResponse<T>>`
- Success: `ResponseEntity.ok(ApiResponse.ok(result))`
- Not found: `ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("message"))`
- Bad request: `ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()))`
```

Guarda el archivo.

#### 8b. Ejecutar el pipeline

Revierte los cambios del paso 4 si no lo has hecho:

```bash
git checkout src/
```

Lanza de nuevo el mismo prompt del paso 4. Deja que el pipeline termine.

#### 8c. Diagnosticar

Revisa el código generado por `feature-implementer`:

```bash
git diff src/main/
```

Responde estas preguntas antes de seguir:

- ¿El nuevo endpoint devuelve `ApiResponse<T>` o devuelve el objeto directamente?
- ¿Los tests de `test-writer` fallan? ¿Con qué error?
- ¿El fallo fue visible en la traza del orquestador o pasó desapercibido?

#### 8d. Corregir la instrucción

Restaura el bloque que eliminaste. Añade además un ejemplo de código concreto debajo de las instrucciones en prosa, de modo que el requisito sea más difícil de ignorar:

```java
// Example — new endpoints must follow this exact pattern:
@GetMapping("/stats")
public ResponseEntity<ApiResponse<TaskStats>> getStats() {
    return ResponseEntity.ok(ApiResponse.ok(taskService.getStats()));
}
```

Vuelve a ejecutar el pipeline y comprueba que ahora el código generado cumple el patrón.

> **Para reflexionar:** ¿Qué fue más útil para diagnosticar el fallo: el error del compilador, el error del test o revisar el código directamente? ¿Qué tipo de instrucción en el subagente (prosa, ejemplo de código, criterio de éxito explícito) fue más efectivo para prevenir el fallo?

---

### Paso 9. Escribir un subagente desde cero · *Nivel: avanzado · ~30 min*

En los pasos anteriores has usado subagentes escritos por otra persona. En este paso escribes uno tú.

El subagente que vas a crear es `change-validator`: un agente de solo lectura que comprueba que el código modificado cumple las reglas del `CLAUDE.md` del proyecto antes de que el orquestador dé el trabajo por terminado.

#### 9a. Crear el archivo

Crea `.claude/agents/change-validator.md`. El agente debe:

- Tener `tools: Read` (solo lectura, no puede modificar nada).
- Leer los archivos que han cambiado en `src/main/`.
- Verificar dos reglas del `CLAUDE.md`:
  1. Los controllers no llaman directamente a métodos del repository.
  2. Todos los endpoints devuelven `ResponseEntity<ApiResponse<T>>`.
- Devolver un informe con: reglas cumplidas, reglas violadas, y para cada violación el archivo y el número de línea aproximado.

La descripción del campo `description` en el frontmatter es importante: el orquestador la lee para decidir cuándo invocar al agente. Redáctala para que quede claro que este agente debe llamarse al final del pipeline, después de que los otros tres hayan terminado.

#### 9b. Integrar en el pipeline

Revierte los cambios y lanza de nuevo el pipeline del paso 4, añadiendo al final del prompt:

```
Cuando los tres subagentes terminen, pide a change-validator que
verifique que el código generado cumple las reglas del proyecto.
```

#### 9c. Probar que detecta violaciones

Para comprobar que el subagente funciona, introduce una violación deliberada después de que `feature-implementer` termine: edita manualmente `TaskController.java` e inyecta el repository en el constructor además del service. No hace falta que lo uses, basta con que aparezca como campo.

Lanza `change-validator` de forma aislada:

```
Ejecuta el subagente change-validator sobre los cambios actuales en src/main/
```

Comprueba que el informe detecta la violación.

> **Para reflexionar:** ¿La descripción que escribiste en el frontmatter fue suficiente para que el orquestador supiera cuándo invocar al agente, o tuviste que ajustarla? ¿Qué añadirías a los otros tres subagentes para que llamen a `change-validator` automáticamente al terminar?

---

## Conclusión

El ejercicio demuestra cuatro cosas:

1. **Instalación:** los subagentes son archivos Markdown con frontmatter YAML. No requieren configuración adicional. El campo `tools` limita lo que cada subagente puede hacer, lo que reduce el riesgo de que actúe fuera de su alcance.

2. **Cascada:** el agente principal coordina los subagentes en orden, pasando el resultado de cada uno al siguiente. Cada subagente trabaja en un contexto separado: `feature-implementer` no sabe que luego vendrá `test-writer`, y `test-writer` no sabe que `documenter` añadirá Javadoc después.

3. **Fragilidad de las instrucciones:** un subagente con instrucciones incompletas produce código incorrecto sin avisar. La calidad de un pipeline multi-agent depende directamente de la calidad de las instrucciones de cada subagente. Los criterios de éxito explícitos y los ejemplos de código son más robustos que la prosa.

4. **Composición:** un subagente nuevo se integra en un pipeline existente sin modificar los demás. El orquestador lo descubre a través del campo `description` y decide cuándo invocarlo.

**La pregunta de fondo:** ¿Qué tareas de tu proyecto cotidiano podrían beneficiarse de este patrón? Piensa en tareas con tres o más pasos donde cada paso tiene criterios de éxito verificables.
