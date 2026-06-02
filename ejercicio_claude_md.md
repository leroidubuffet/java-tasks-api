# Ejercicio: CLAUDE.md

---

## Repositorios del ejercicio

**Python:** `python-claude-client` — https://github.com/leroidubuffet/python-claude-client
**Java:** `java-tasks-api` — https://github.com/leroidubuffet/java-tasks-api

---

## Parte 1. Descubrimiento

### Paso 1. `/init` en un repositorio vacío

Crea una carpeta vacía e inicia Claude Code dentro:

```
mkdir repo-vacio
cd repo-vacio
claude
```

Ejecuta el comando:

```
/init
```

Observa el CLAUDE.md generado. Sin código que analizar, Claude no puede inferir ningún comando, ninguna arquitectura ni ninguna convención del equipo.

> **Para reflexionar:** ¿Qué información falta en el documento? ¿Qué tendrías que escribir a mano para que fuera útil?

---

### Paso 2. `/init` en el repositorio real

Elige uno de los dos repositorios y clónalo:

**Python**
```
git clone https://github.com/leroidubuffet/python-claude-client
cd python-claude-client
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
export ANTHROPIC_API_KEY=tu_clave
claude
```

**Java**
```
git clone https://github.com/leroidubuffet/java-tasks-api
cd java-tasks-api
claude
```

Ejecuta de nuevo el mismo comando:

```
/init
```

Compara el resultado con el del paso anterior. Claude ahora detecta el stack, las dependencias, los comandos de instalación y test, y los patrones arquitectónicos del proyecto.

> **Para reflexionar:** ¿Hay algo importante que haya pasado por alto? ¿Cambiarías o añadirías algo al CLAUDE.md generado?

---

## Parte 2. Observación

### Paso 3. Verificar que la aplicación funciona

Antes de modificar nada, comprueba que el proyecto arranca.

```
/run
```

> **Qué hace `/run`:** Arranca la aplicación tal como lo haría un usuario real. No ejecuta los tests, no importa funciones sueltas. Para un servidor, lanza el proceso, espera a que esté listo y lanza peticiones de prueba contra sus endpoints. La diferencia con `mvn test` o `pytest` es que comprueba que el sistema funciona de extremo a extremo, no que las piezas pasan sus aserciones por separado.

Para Python, Claude ejecutará `main.py` con un mensaje de prueba. Para Java, arrancará el servidor en el puerto 8080 y lanzará algunas peticiones curl.

Una vez que la aplicación esté corriendo, responde esta pregunta mirando el CLAUDE.md del Paso 2:

> **Para reflexionar:** ¿El CLAUDE.md generado contenía los comandos correctos para arrancar la aplicación? ¿Habría podido un desarrollador nuevo seguirlos sin ayuda adicional?

---

### Paso 4. Revisión de código sin reglas propias

Con la aplicación funcionando, pide una revisión del código:

```
/code-review
```

> **Qué hace `/code-review`:** Lanza varios agentes en paralelo, cada uno revisando el código desde un ángulo diferente: revisión de bugs línea a línea, auditoría de comportamientos eliminados que deberían permanecer, trazado entre ficheros, reimpolementaciones, eficiencia, simplificación. Los hallazgos se verifican antes de mostrarse: un agente propone problemas (candidatos), otro lo confirma o lo descarta. Todo en contextos separados. El resultado es una lista priorizada de problemas potenciales, no sugerencias de estilo.

Anota **los tres hallazgos que te parezcan más relevantes**. No hace falta procesar la lista entera. Lo que importa ahora es el punto de partida.

> **Para reflexionar:** ¿Qué criterios ha usado Claude cuando no tiene instrucciones propias del proyecto? ¿Ha detectado algo que solo tiene sentido conociendo el contexto del equipo, o son todos criterios genéricos?

---

## Parte 3. Control

### Paso 5. Añadir reglas a CLAUDE.md

Abre CLAUDE.md y añade una sección `## Project rules` con estas dos reglas:

**Python**
```markdown
## Project rules

- Nunca registres el contenido de los mensajes en los logs. Solo metadatos: rol,
  modelo y recuento de tokens. La función de log está en src/utils.py.
- Todas las llamadas a la API deben pasar por src/client.py. Nunca instancies
  anthropic.Anthropic() directamente desde otro módulo.
```

**Java**
```markdown
## Project rules

- Los controllers nunca llaman directamente a métodos del repository.
  Toda la lógica pasa por el service.
- Todos los endpoints devuelven un objeto ApiResponse<T>.
  Nunca se devuelve el modelo directamente.
```

> **Nota:** La precisión importa. Indica dónde vive cada función o clase relevante. Las reglas vagas ("mantén el código limpio") se ignoran; las reglas concretas se aplican.

---

### Paso 6. Comprobar que las reglas se aplican

Este paso tiene dos fases.

#### Fase A: Verificar el código existente

Pide a Claude que compruebe si el código actual cumple las reglas que acabas de definir:

```
/verify
```

> **Qué hace `/verify`:** Arranca la aplicación, la conduce hasta los puntos de código que quiere comprobar y captura lo que observa. No lee el código y razona sobre él, sino que lo ejecuta. Para verificar una regla como "nunca se loguea contenido de mensajes", lanza la app, provoca una interacción real y comprueba lo que aparece en el log.

> **Para reflexionar:** ¿Cumple el código ya las dos reglas? ¿Ha encontrado Claude algún caso que las viola o las roza?

#### Fase B: Generar código nuevo y observar si respeta las reglas

Ahora pide a Claude que añada funcionalidad nueva:

**Python**
```
Añade una función que guarde el historial de la conversación en un archivo de texto
```

**Java**
```
Añade un endpoint que devuelva las tareas agrupadas por estado
```

Observa el código que genera Claude.

> **Para reflexionar:** ¿Ha respetado las reglas de CLAUDE.md?
>
> - En Python: ¿el historial escribe contenido de mensajes en el log, violando la Regla 1? ¿Lo ha detectado Claude y lo ha señalado?
> - En Java: ¿el nuevo endpoint pasa por el service, o accede al repository directamente? ¿La respuesta está envuelta en `ApiResponse<T>`?
>
> Si Claude ha violado una regla, reformúlala hasta que la cumpla. ¿Qué cambio en la redacción marca la diferencia?

---

### Paso 7. Refactorización guiada por las reglas

Ejecuta la simplificación automática:

```
/simplify
```

> **Qué hace `/simplify`:** Lanza cuatro agentes en paralelo, cada uno buscando un tipo de problema de calidad: código duplicado que ya existe en otro sitio, complejidad innecesaria, trabajo repetido que se podría hacer una sola vez, y lógica implementada a la capa equivocada. No busca bugs, eso la hace `/code-review`. Busca código que funciona pero que se puede expresar de forma más limpia o más profunda.

Cuando termine, revisa las sugerencias que ha hecho Claude.

> **Para reflexionar:** ¿Alguna sugerencia del Paso 7 viola las reglas que escribiste en el Paso 5? ¿Habría violado alguna las sugerencias del Paso 4?
>
> `/code-review` y `/simplify` no tratan lo mismo — uno busca bugs, el otro busca código que funciona pero está mal expresado — así que sus salidas no son comparables directamente. Lo que sí es comparable es si Claude respeta el contexto del proyecto al hacer sus propuestas.
>
> Sin CLAUDE.md, Claude no sabe qué decisiones son intencionales: podría sugerir mover validación directamente a la entidad sin saber que el equipo ha decidido que esa lógica vive en el service, o proponer un atajo que salte una capa. Con CLAUDE.md, esas sugerencias deberían respetar las reglas definidas.
>
> Esa diferencia es el efecto del CLAUDE.md.

---

## Conclusión

El ejercicio demuestra tres cosas en secuencia:

1. **Descubrimiento:** `/init` sobre código real genera documentación automáticamente. El valor está en que Claude lee el código, no las intenciones del programador.

2. **Observación:** Sin CLAUDE.md con reglas propias, Claude aplica criterios universales. Detecta bugs y mejoras técnicas, pero no conoce las decisiones de diseño del equipo.

3. **Control:** Con reglas explícitas en CLAUDE.md, Claude las aplica en revisión, verificación y generación de código nuevo. Las reglas concretas funcionan; las vagas no.

**La pregunta de fondo:** ¿Qué conocimiento del proyecto no está en el código y nunca va a aparecer en un `/init`? Ese conocimiento es exactamente lo que debemos asegurarnos de añadir a CLAUDE.md.
