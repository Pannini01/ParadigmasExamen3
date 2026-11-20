# Calidad del proyecto

**Plantilla preparada para la wiki; todavía no publicada.**

Proyecto: Exámenes de Sangre. Análisis local final: 19/11/2026 18:58:06 COT. SonarQube Community Build 25.1.0.102122. Quality Gate: **OK**.

| Indicador | Resultado |
|---|---:|
| Duplicación global | 0,0 % (máximo exigido: 1 %) |
| Cobertura global | 97.4 % |
| Pruebas aprobadas | 38 de 38 |
| Bugs / vulnerabilidades / code smells | 0 / 0 / 0 |

## Imagen resumen

Adjuntar a esta wiki `docs/evidencias/sonarqube-resumen.png` y reemplazar el destino del siguiente enlace por la ubicación real del adjunto:

![Resumen de métricas reales de SonarQube](sonarqube-resumen.png)

La imagen se elaboró a partir de la API de SonarQube; no simula una captura del dashboard. Los JSON originales, el identificador de análisis y los logs se encuentran en `docs/evidencias/` del repositorio. El análisis se ejecutó localmente con perfil Sonar way; si se analiza otra revisión o se usa otro servidor, actualizar esta página con los resultados correspondientes.

ID de análisis: `0c97f978-a75b-41f6-a609-f9fa77ffa6a9`.

## Reproducción

Con JDK 21, Maven y SONAR_TOKEN configurado, ejecutar desde la raíz del proyecto:

```bash
mvn clean verify sonar:sonar "-Dsonar.host.url=http://localhost:9000" "-Dsonar.qualitygate.wait=true"
```

Asignar la condición global `duplicated_lines_density > 1` como error. Revisar `docs/VERIFICACION.md` y README para conocer el entorno y alcance. El enlace local del log no es un servicio público.
