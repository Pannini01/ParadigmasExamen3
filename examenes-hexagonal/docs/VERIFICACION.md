# Verificación de la entrega

Análisis final: **19/11/2026 18:58:06 COT**. Proyecto: `examenes-hexagonal`.

Se ejecutaron compilación y pruebas con Maven 3.9.9 / JDK Temurin 21.0.13.1+2 sobre Linux. JavaFX 21.0.5; JUnit 5.11.4; JaCoCo 0.8.12; SonarScanner for Maven 5.0.0.4389. SonarQube Community Build **25.1.0.102122**, perfil Sonar way de Java. En la preparación inicial, la versión 26.11 no pudo iniciarse porque el entorno no ofrece `/proc/self/coredump_filter`; se usó una instancia local de prueba compatible (25.1), ligada a localhost y con base H2. No es una instalación de producción.

## Resultado medido

| Métrica | Resultado |
|---|---:|
| Duplicación global (%) | 0.0 |
| Bloques duplicados | 0 |
| Cobertura global SonarQube (%) | 97.4 |
| Cobertura de líneas (%) | 97.3 |
| Cobertura de ramas (%) | 97.6 |
| Pruebas ejecutadas | 38 |
| Fallos de pruebas | 0 |
| Errores de pruebas | 0 |
| Pruebas omitidas | 0 |
| Errores de fiabilidad | 0 |
| Vulnerabilidades | 0 |
| Code smells | 0 |
| Security hotspots | 0 |
| Líneas de código (ncloc) | 307 |

**Quality Gate: OK**. Las cifras se extrajeron de la API después de que la tarea de análisis terminó en SUCCESS. No son una estimación ni un chequeo por comparación textual.

| Condición | Umbral de error | Valor medido | Estado |
|---|---|---|---|
| `new_coverage` | LT 80 | 93.8 | OK |
| `coverage` | LT 80 | 97.4 | OK |
| `new_duplicated_lines_density` | GT 3 | 0.0 | OK |
| `duplicated_lines_density` | GT 1 | 0.0 | OK |
| `bugs` | GT 0 | 0 | OK |
| `new_violations` | GT 0 | 0 | OK |
| `vulnerabilities` | GT 0 | 0 | OK |

La condición de duplicación es global y se activa cuando supera 1 %. Las métricas de código nuevo requieren una línea base y no sustituyen este control global. La cobertura global de SonarQube combina líneas y condiciones; por eso 97.4 % global no contradice el 97.3 % de cobertura de líneas de JaCoCo.

## Conservación de la vista

Se restauró el FXML original, sustituyendo únicamente su referencia `fx:controller`. Se mantienen estilos, textos, distribución, campos y los cuatro botones originales. App conserva la escena de 640 × 580 y la ventana no redimensionable. La prueba `conservaFXMLOriginalSalvoReferenciaDelControlador` comprueba el SHA-256 contra el FXML recibido tras normalizar exclusivamente esa referencia. No se añadieron controles gráficos para limpiar o cambiar el orden; Strategy se configura mediante `examenes.orden` al iniciar.

## Evidencias y alcance

- [Resumen visual](evidencias/sonarqube-resumen.png), elaborado desde los JSON originales; **no es una captura de pantalla del dashboard**.
- [Métricas originales](evidencias/sonarqube-metricas.json), [Quality Gate](evidencias/sonarqube-quality-gate.json), [tarea completada](evidencias/sonarqube-tarea.json), [incidencias abiertas](evidencias/sonarqube-incidencias.json) y [versión](evidencias/sonarqube-version.json).
- [Ejecución de Maven, pruebas y scanner](evidencias/maven-verify-sonar.log).
- [Reporte de cobertura](evidencias/cobertura/index.html) y [JaCoCo XML](evidencias/jacoco.xml).
- Reportes JUnit XML y texto en `evidencias/pruebas/`.
- [Captura real de la aplicación](evidencias/aplicacion.png), tomada desde JavaFX con datos ficticios.
- [Hashes SHA-256](evidencias/sha256-fuentes.txt) de fuentes, recursos, POM y workflow que acompañan la medición.
- JavaDoc: regenerado con `mvn javadoc:javadoc`; salida en `evidencias/javadoc.log`, sitio generado en `javadoc/index.html`.

ID de análisis: `0c97f978-a75b-41f6-a609-f9fa77ffa6a9`. ID de tarea: `09a98eab-c0e3-43d7-bd50-828101310d90`.

Ámbito del analizador: `sonar.sources=src/main`; `sonar.tests=src/test`. No hay exclusiones CPD, exclusiones de cobertura ni `NOSONAR` en producción. El scanner clasifica los archivos por sus lenguajes soportados; FXML se prueba mediante carga e interacción JavaFX. El proyecto original, los documentos y las evidencias no se mezclan con el código de producción analizado.

El resultado es válido para esta versión de fuentes, servidor y perfil de reglas. Al publicar en otro servidor, repetir el análisis y comprobar su Quality Gate. El servidor local de esta ejecución es temporal: una URL localhost de los logs no es un enlace público para el docente.

## Estado de publicación

El código, los entregables y la imagen están preparados. **GitHub y su wiki aún no se han publicado**: no se proporcionó el repositorio de destino ni una conexión autorizada. La plantilla está en `wiki/Calidad.md`. El workflow de GitHub Actions tampoco ha corrido en remoto; las ejecuciones documentadas fueron locales.
