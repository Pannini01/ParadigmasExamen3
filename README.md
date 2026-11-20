# Exámenes de Sangre - Arquitectura Hexagonal

Aplicación JavaFX para registrar exámenes, listarlos y conservar una sesión en XML. Se desarrolló a partir de `ParadigmasExamen3-main.zip`: el original contenía el formulario y manejadores vacíos. El diagnóstico distingue ese estado inicial de las funciones implementadas en esta entrega.

## Vista original conservada

Esta revisión restaura exactamente el FXML recibido: mismos campos, cuatro botones, textos, estilos, dimensiones de ventana (640 × 580) y ventana no redimensionable. El único cambio dentro del FXML es `fx:controller`, que enlaza el adaptador refactorizado. No se añade el botón Limpiar ni el selector de orden que tenía la primera actualización.

La refactorización se realiza en el controlador, los casos de uso, el dominio y la persistencia. Una prueba verifica el hash del FXML original después de normalizar únicamente la referencia al controlador. Se mantienen las restricciones de espacio que ya presentaba el diseño recibido; no se rediseñó el formulario.

## Requisitos

- **JDK 21 completo** (incluye `javac` y `javadoc`), no solo JRE.
- Maven 3.9 o superior, con acceso a Maven Central en la primera compilación.
- Windows, Linux o macOS con entorno gráfico para usar la aplicación.
- SonarQube y un token de análisis para repetir la revisión de calidad.

JavaFX 21.0.5 se descarga mediante Maven. No se requiere base de datos, copiar JAR manualmente ni configurar un JavaFX SDK externo. El proyecto no usa `module-info.java`; Maven configura la ejecución JavaFX.

## Instalar, verificar y ejecutar

1. Descomprimir el proyecto o clonar su repositorio una vez publicado.
2. Abrir una terminal **en la carpeta que contiene `pom.xml`**.
3. Confirmar que `java -version`, `javac -version` y `mvn -version` utilizan JDK 21. `JAVA_HOME` debe apuntar a ese JDK.
4. Ejecutar:

```bash
mvn clean verify
mvn javafx:run
```

`clean verify` compila, ejecuta pruebas y genera cobertura JaCoCo. Las pruebas de JavaFX usan Monocle sin pantalla física; no sustituyen una comprobación visual en el equipo de la sustentación.

Para generar JavaDoc:

```bash
mvn javadoc:javadoc
```

Abrir `target/reports/apidocs/index.html`. El reporte de cobertura está en `target/site/jacoco/index.html`; los resultados JUnit, en `target/surefire-reports/`.

### Importar en Eclipse

1. `File > Import > Maven > Existing Maven Projects`.
2. Seleccionar esta carpeta y su `pom.xml`.
3. En `Preferences > Java > Installed JREs`, añadir y seleccionar el JDK 21. Revisar también el JRE de las configuraciones de ejecución Maven.
4. Clic derecho al proyecto: `Maven > Update Project`.
5. `Run As > Maven build...`; en **Goals** escribir `clean javafx:run` y ejecutar.
6. Para las pruebas, crear otra ejecución Maven con **Goals** `clean verify`.

Usar la ejecución Maven indicada evita iniciar JavaFX sin sus componentes de runtime. No se debe importar el módulo antiguo `Examen2` dentro del proyecto nuevo.

## Configuración y uso

El destino predeterminado es `datos/examenes.xml`, relativo a la raíz del proyecto cuando se ejecuta con Maven. Se crea al serializar por primera vez. Puede cambiarse:

```bash
mvn javafx:run "-Dexamenes.archivo=C:/MisDatos/examenes.xml"
```

En Linux/macOS, usar una ruta del sistema, por ejemplo `/home/usuario/examenes.xml`. La ubicación se configura en el punto de composición `bootstrap.App`, mediante la propiedad `examenes.archivo` pasada por Maven.

| Acción | Resultado |
|---|---|
| Guardar | Agrega el examen a la sesión en memoria. Rechaza códigos duplicados. |
| Listar | Muestra una copia ordenada por código o paciente. |
| Serializar | Conserva la sesión completa y reemplaza el archivo XML anterior. |
| Deserializar | Reemplaza la sesión con el archivo, solo si se lee y valida completamente. |
| Cerrar | Finaliza la aplicación. Los cambios no serializados no se conservan. |

Para recuperar datos al volver a abrir, pulsar **Deserializar**. No hay carga automática ni guardado automático. Deserializar descarta los cambios de sesión no serializados; serialice antes si necesita conservarlos. Si el archivo no existe, está corrupto o contiene duplicados, se muestra un error y se conserva la sesión actual.

### Reglas incorporadas

- Código: obligatorio, de 1 a 40 caracteres, sin controles; se recortan espacios externos y se normaliza a mayúsculas. `ex-01` y `EX-01` representan el mismo código.
- Paciente: obligatorio, de 1 a 120 caracteres sin controles; admite nombres Unicode.
- Costo: no negativo. En pantalla se admite punto o coma decimal, hasta dos cifras, sin separador de miles. El dominio usa `BigDecimal` y escala 2 sin redondeo silencioso. Se admite costo cero.
- Grupo sanguíneo: A, B, AB u O. Factor Rh: positivo o negativo.
- Formato XML versión 1, máximo 10000 registros por archivo. Este límite corresponde al adaptador de archivo.
- La aplicación registra información; no interpreta resultados clínicos ni determina compatibilidad sanguínea.

Estas reglas no estaban implementadas en el original. No se añadieron edición, eliminación, autenticación ni uso concurrente: la interfaz original no incluía esas operaciones. La aplicación trabaja en un único hilo y conserva instantáneas locales.

### Orden del listado sin modificar la vista

La estrategia predeterminada es `CODIGO`. Para ordenar por paciente, iniciar con:

```bash
mvn javafx:run "-Dexamenes.orden=PACIENTE"
```

Valores admitidos: `CODIGO` y `PACIENTE` (sin distinguir mayúsculas). App inyecta el criterio en el controlador. El botón **Listar** original utiliza esa estrategia. Para cambiarla desde la configuración, cerrar y volver a iniciar; no se introduce ningún control gráfico adicional.

## Arquitectura y patrones

![Arquitectura hexagonal](docs/arquitectura2.png)

| Paquete | Responsabilidad |
|---|---|
| `dominio` | `Examen` inmutable, invariantes, `TipoSangre` y `FactorRh`. |
| `aplicacion` | `ServicioExamenes`, sesión y operaciones; catálogo `OrdenExamen`. |
| `aplicacion.puerto.entrada` | `RegistroExamenes`, contrato que usa la interfaz. |
| `aplicacion.puerto.salida` | `AlmacenExamenes` y error de persistencia independiente de infraestructura. |
| `adaptador.entrada` | `ControladorExamenes`, traducción de entradas y mensajes JavaFX. |
| `adaptador.salida` | `ArchivoXmlExamenes`, mapeo del modelo a Properties XML y acceso NIO. |
| `bootstrap` | `App`, construcción e inyección manual de las dependencias. |

El núcleo depende de contratos, sin importar JavaFX ni APIs de archivos. `ArquitecturaTest` revisa esas dependencias en los imports. Es una comprobación de paquetes/imports, no una demostración formal de toda dependencia posible.

| Categoría | Patrón GoF | Aplicación real |
|---|---|---|
| Creacional | Builder | `Examen.Builder` construye datos por nombre y delega la validación al producto. |
| Estructural | Adapter | `ArchivoXmlExamenes` traduce el puerto de aplicación a `Properties` y NIO. |
| Comportamiento | Strategy | `Comparator<Examen>` define el contrato; `OrdenExamen` ofrece los órdenes por código y paciente. |

Builder se utiliza en la interfaz y en la recuperación de archivos. Strategy se configura al iniciar con `examenes.orden` y el servicio recibe el comparador sin condiciones por tipo. La documentación de participantes, motivos y costos está en [actividad3.pdf](docs/actividad3.pdf).

## Pruebas y evidencias

La versión entregada incluye pruebas de dominio y casos de uso, integración del adaptador XML, interacción con los botones JavaFX y comprobación de imports entre capas.

| Suite | Casos ejecutados |
|---|---:|
| `ExamenTest` | 10 |
| `ServicioExamenesTest` | 8 |
| `ArchivoXmlExamenesTest` | 16 |
| `VistaTest` | 3 |
| `ArquitecturaTest` | 1 |
| **Total** | **38** |

Resultados: [evidencia de verificación](docs/VERIFICACION.md). Se conservan la salida Maven, reportes JUnit y cobertura en `docs/evidencias/`. Son evidencia de esta entrega; `target/` se regenera al ejecutar Maven. La captura de la aplicación proviene de una ejecución real de la vista original restaurada con datos ficticios de demostración.

El flujo de GitHub Actions `.github/workflows/pruebas.yml` ejecuta compilación, pruebas y JavaDoc al publicar cambios. Su resultado remoto solo existirá después de subir el proyecto al repositorio.

## Análisis con SonarQube

1. Iniciar una instancia de SonarQube compatible con su entorno, crear el proyecto con clave `examenes-hexagonal` y generar un token.
2. Definir `SONAR_TOKEN` como variable de entorno local. No escribirlo en el código, no guardarlo en el repositorio y no compartirlo en capturas.
3. Ejecutar desde esta carpeta:

```bash
mvn clean verify sonar:sonar "-Dsonar.host.url=http://localhost:9000" "-Dsonar.qualitygate.wait=true"
```

Si usa un servidor remoto, reemplazar la URL. El `pom.xml` ya configura el XML de JaCoCo. SonarQube importa la cobertura generada por JaCoCo; no ejecuta las pruebas por sí mismo.

4. Configurar y asignar una Quality Gate con la condición de **duplicación global mayor que 1 % = error** (`duplicated_lines_density > 1`). Revisar también la duplicación de código nuevo cuando haya una línea base. No basta con el umbral predeterminado de otro Quality Gate.
5. Abrir las medidas del proyecto y confirmar que el análisis final corresponde al código entregado. El ámbito de producción es `src/main`; `src/test` se clasifica como pruebas. No se configuraron exclusiones CPD ni supresiones `NOSONAR` en producción para reducir el porcentaje.
6. Guardar una captura legible con proyecto, fecha, Quality Gate, duplicación y cobertura. Las métricas y versión de la ejecución local están en `docs/VERIFICACION.md`.

El porcentaje de SonarQube se refiere a bloques que detecta su analizador, no significa que nunca se repita una línea breve. Para Java, la detección usa secuencias de al menos diez sentencias duplicadas. Cambiar indentación o textos no constituye una solución a duplicación.

## Entregables

| Actividad | Archivos |
|---|---|
| 1 | `docs/actividad1.pdf`, diagnóstico y arquitectura original. |
| 2 | `docs/dominio2.png`, `docs/arquitectura2.png`. |
| 3 | `docs/dominio3.png`, `docs/actividad3.pdf`. |
| 4 | Código, pruebas, JavaDoc, README, configuración SonarQube y `docs/evidencias/`. |
| 5 | `docs/SUSTENTACION.md`, guion con demostración y preguntas. |

Se incluyen fuentes Graphviz `.dot` y versiones vectoriales `.svg` de los diagramas. Para renderizar una fuente si se instala Graphviz: `dot -Tpng docs/dominio3.dot -o docs/dominio3.png`.

## Publicación en GitHub y wiki

El ZIP está preparado para un repositorio, pero **no constituye una publicación en GitHub**. No se recibió la URL del repositorio ni se configuró acceso a una cuenta. Tampoco se ha publicado su wiki. La plantilla de página está en `docs/wiki/Calidad.md`.

1. En el repositorio elegido, subir el contenido de esta carpeta, incluyendo `.github`, código y `docs`, sin `target/`, `datos/` ni tokens.
2. Ejecutar el workflow y comprobar su resultado.
3. Habilitar la wiki, crear la página **Calidad**, copiar el contenido de la plantilla y adjuntar la imagen resumen `docs/evidencias/sonarqube-resumen.png`.
4. Ajustar el enlace de la imagen al adjunto real de la wiki y guardar la página. Verificar el enlace publicado.

Si el repositorio ya tiene historial, integrar estos archivos en una rama conservando ese historial; no se recomienda reemplazarlo por un repositorio nuevo sin revisión.

## Declaración de uso de IA

Se utilizó **OpenAI ChatGPT (agente Codex)** como apoyo para inspeccionar el ZIP original, proponer y aplicar la arquitectura, implementar patrones y pruebas, redactar JavaDoc y README, generar diagramas e informes y ejecutar/verificar herramientas. La IA no es el analizador SonarQube: Maven/JUnit, JaCoCo y SonarQube generan sus resultados mediante ejecución independiente. No se generaron imágenes ficticias como evidencia de ejecución.

La persona que presenta el proyecto debe revisar el código y las reglas adoptadas, comprender las decisiones, repetir la ejecución en su equipo y poder explicar los patrones y resultados. La revisión académica final del estudiante no se da por realizada en este documento.

## Referencias técnicas

- [OpenJFX: guía oficial](https://openjfx.io/openjfx-docs/).
- [SonarQube: cobertura Java](https://docs.sonarsource.com/sonarqube-community-build/analyzing-source-code/test-coverage/java-test-coverage).
- [SonarQube: definición de métricas](https://docs.sonarsource.com/sonarqube-server/10.8/user-guide/code-metrics/metrics-definition).
- Gamma, Helm, Johnson y Vlissides. *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley, 1994.
