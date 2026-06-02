# Ejercicio: CLAUDE.md

Repositorios del ejercicio:
- Python: `python-claude-client` — https://github.com/leroidubuffet/python-claude-client
- Java: `java-tasks-api` — https://github.com/leroidubuffet/java-tasks-api

---

## Parte 1. Descubrimiento

### Paso 1. /init en un repositorio vacio

Crea una carpeta nueva e inicia Claude Code dentro:

```
mkdir repo-vacio
cd repo-vacio
claude
```

Ejecuta el comando:

> */init*

Observa el `CLAUDE.md` generado. Sin codigo que analizar, Claude produce un documento generico y poco util.

**Para reflexionar:** Que informacion echa en falta el documento? Que tendrias que escribir a mano?

---

### Paso 2. /init en un repositorio real

Elige uno de los dos repositorios y clonalo:

**Python**

```
git clone https://github.com/leroidubuffet/python-claude-client
cd python-claude-client
python3 -m venv .venv && source .venv/bin/activate
pip install -r requirements.txt
claude
```

**Java**

```
git clone https://github.com/leroidubuffet/java-tasks-api
cd java-tasks-api
claude
```

Ejecuta de nuevo el mismo comando:

> */init*

Compara el resultado con el del paso anterior. Claude ahora detecta el stack, las dependencias, los comandos de instalacion y test, y las convenciones del proyecto.

**Para reflexionar:** Que ha detectado Claude que no estaba en el repo vacio? Hay algo importante que haya pasado por alto?

---

## Parte 2. Observacion

### Paso 3. Verificar que la aplicacion funciona

Antes de modificar nada, comprueba que el proyecto arranca tal cual.

_Nota para Python: necesitas `ANTHROPIC_API_KEY` exportada en el terminal antes de ejecutar._

> */run*

Para Python, Claude ejecutara `main.py` con un mensaje de prueba. Para Java, arrancara el servidor en el puerto `8080`.

**Para reflexionar:** El `CLAUDE.md` generado en el paso anterior contenia los comandos correctos para arrancar la app?

---

### Paso 4. Revision de codigo sin reglas propias

Para Java, puedes parar el servidor antes de continuar. Pide una revision del codigo:

> */code-review*

Anota los puntos que senala Claude. Este es el punto de partida, sin ninguna instruccion personalizada en `CLAUDE.md`.

**Para reflexionar:** Que criterios usa Claude cuando no tiene instrucciones propias del proyecto?

---

## Parte 3. Control

### Paso 5. Anadir reglas a CLAUDE.md

Abre `CLAUDE.md` y anade una seccion con estas reglas:

**Python**
- Nunca registres el contenido de los mensajes en los logs. Solo metadatos: rol, modelo y recuento de tokens. La funcion de log esta en `src/utils.py`.
- Todas las llamadas a la API deben pasar por `src/client.py`. Nunca instancies `anthropic.Anthropic()` directamente desde otro modulo.

**Java**
- Los controllers nunca llaman directamente a metodos del repository. Toda la logica pasa por el service.
- Todos los endpoints devuelven un objeto `ApiResponse<T>`. Nunca se devuelve el modelo directamente.

---

### Paso 6. Comprobar que las reglas se aplican

Pide a Claude que compruebe si el codigo cumple las reglas que acabas de definir:

> */verify*

Si quieres ir mas lejos, prueba con un prompt directo:

**Python**

> *Anade una funcion que guarde el historial de la conversacion en un archivo de texto*

**Java**

> *Anade un endpoint que devuelva las tareas agrupadas por estado*

**Para reflexionar:** Claude ha respetado las reglas? Si no lo ha hecho, como tendrias que reformularlas para que funcionen?

---

### Paso 7. Refactorizacion guiada por las reglas

Ejecuta la simplificacion automatica y compara el resultado con la revision del paso 4:

> */simplify*

**Para reflexionar:** Ha cambiado el tipo de sugerencias respecto al paso 4? Claude ahora conoce el contexto del proyecto.
