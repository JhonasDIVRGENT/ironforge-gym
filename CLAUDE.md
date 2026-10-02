# CLAUDE.md

Este proyecto utiliza AGENTS.md como documento principal de arquitectura,
convenciones y reglas de desarrollo. Las reglas comunes están solo allí; este
archivo agrega únicamente indicaciones para Claude Code.

Antes de crear, modificar o eliminar código:

1. Leer AGENTS.md completo.
2. Respetar su arquitectura (model, service, dao, session, app).
3. No crear paquetes, frameworks o clases adicionales sin autorización.
4. Mantener compatibilidad con Java 21 y Maven.
5. Ejecutar `mvn clean compile` después de cambios relevantes. No usar
   `mvn test` ni crear pruebas con JUnit u otros frameworks: la verificación
   se hace con las demostraciones de `app` (ver AGENTS.md, secciones 12 y 15).

Si una instrucción de una tarea entra en conflicto con AGENTS.md o con el UML
de `docs/diagramas/`, detenerse y señalar el conflicto antes de modificar la
arquitectura.

## Indicaciones propias de Claude Code

- Entorno Windows: la herramienta PowerShell es la principal. En
  `mvn exec:java` el argumento `"-Dexec.mainClass=..."` va entre comillas.
- Para leer el informe PDF, `pdftotext` (Git Bash/ucrt64) funciona; dejar el
  texto extraído en el directorio temporal de la sesión, no en el repositorio.
- No mostrar, copiar ni citar las credenciales de `ConexionDB` en
  respuestas, commits o documentación.
- No ejecutar scripts de `database/` ni sentencias que escriban sobre la base
  `ironforge_gym`. No ejecutar contra MySQL las demostraciones que registran
  datos sin autorización explícita del usuario para esa ejecución.
- Las demostraciones piden datos por teclado: para ejecutarlas de forma
  automática, pasar la entrada por tubería desde un archivo del directorio
  temporal, sin escribir credenciales en el repositorio.
- No hacer commits ni push salvo petición explícita.
