# Guion de sustentación (10 a 12 minutos)

La vista conserva el diseño recibido. La actualización cambia su conexión al controlador y completa las operaciones, sin añadir botones ni modificar estilos.

## 1. Proyecto original (1 minuto)

Mostrar `actividad1.pdf` y su arquitectura. Explicar que se recibió un formulario JavaFX con manejadores vacíos, no un sistema funcional. Mostrar el error de `mainClass`, la ausencia de modelo/persistencia/pruebas y la mezcla de versiones. Aclarar que se completó funcionalidad además de refactorizar la presentación.

## 2. Arquitectura y dominio (2 minutos)

Abrir `arquitectura2.png` y `dominio2.png`.

- Dominio: Examen es inmutable y valida código, paciente, costo, grupo y Rh.
- Aplicación: ServicioExamenes implementa los casos de uso y mantiene la sesión.
- Entrada: la GUI llama a RegistroExamenes.
- Salida: el servicio depende de AlmacenExamenes; ArchivoXmlExamenes implementa el contrato.
- App conecta implementaciones; el dominio no conoce JavaFX ni archivos.

Seguir el flujo Guardar: FXML → controlador → Builder → puerto de entrada → servicio → sesión. Seguir Serializar: servicio → puerto de salida → adaptador XML → archivo. Distinguir el flujo de ejecución de la dirección de las dependencias del código.

## 3. Patrones GoF (3 minutos)

Mostrar `dominio3.png` y una clase por patrón.

| Patrón | Qué mostrar | Justificación |
|---|---|---|
| Builder | `Examen.Builder`, `build()` y constructor canónico | Los datos se suministran por nombre; interfaz y recuperación comparten invariantes. |
| Adapter | `AlmacenExamenes` y `ArchivoXmlExamenes` | Traduce listas de exámenes al formato Properties XML y errores de infraestructura al contrato de aplicación. |
| Strategy | `OrdenExamen` y `ServicioExamenes.listar` | Dos comparadores intercambiables; el servicio no contiene condiciones según el orden elegido. |

No confundir el patrón Adapter con toda la arquitectura hexagonal. No presentar Singleton ni Factory Method: no fueron los tres patrones seleccionados. Explicar el costo de Builder (más código) y por qué no se añadió un Director independiente.

## 4. Demostración (3 minutos)

Ejecutar `mvn javafx:run` y usar datos ficticios:

1. Registrar `EX-02`, `Ana Pérez`, `100,50`, grupo O, Rh negativo.
2. Reemplazar los valores del formulario y registrar `EX-01`, `Zulma López`, `150.00`, grupo AB, Rh positivo.
3. Listar por código: EX-01 aparece primero. Para demostrar el criterio por paciente, serializar, cerrar y ejecutar `mvn javafx:run "-Dexamenes.orden=PACIENTE"`; deserializar y listar: Ana aparece primero.
4. Intentar guardar `ex-01` otra vez: se rechaza por código duplicado. Mostrar que no se pierde el original.
5. Probar costo negativo o nombre vacío: se presenta un error de validación.
6. Serializar. Cerrar y abrir; pulsar Deserializar y mostrar ambos registros.
7. Explicar que Deserializar reemplaza los cambios de sesión no serializados. No hay botón Limpiar en la vista original; para otro registro se editan los mismos campos.

No utilizar datos de pacientes reales. Mantener una copia del XML antes de hacer demostraciones de corrupción manual.

## 5. Evidencia (2 minutos)

Ejecutar `mvn clean verify` y mostrar la suma de casos, fallos, errores y omisiones. Abrir `target/site/jacoco/index.html` y distinguir cobertura de líneas y de ramas. Explicar una prueba relevante: un archivo inválido no debe borrar la sesión actual.

Abrir SonarQube o la imagen resumen de la ejecución documentada. Mostrar versión, proyecto, cobertura, Quality Gate y duplicación global. El criterio pedido es máximo 1 %; no presentar una estimación local como resultado SonarQube. Mostrar la wiki publicada cuando exista, no una plantilla local como si fuera la página remota.

## Preguntas esperadas

**¿Por qué BigDecimal?** Conserva el importe decimal y permite controlar su escala sin redondeos binarios.

**¿Dónde se valida la unicidad?** En el servicio, porque requiere comparar el examen con la sesión; la entidad valida un examen individual.

**¿Qué pasa si falla la recuperación?** Se construye y valida una colección nueva; la referencia de sesión se cambia solo al terminar con éxito.

**¿Cómo cambiar a una base de datos?** Implementar AlmacenExamenes con la nueva tecnología y cambiar la composición en App. Se debe respetar el contrato de instantáneas o rediseñar explícitamente el puerto si se requieren operaciones incrementales.

**¿Es el sistema concurrente o clínico?** No. Es un registro académico local de un solo hilo; no realiza decisiones clínicas.

**¿Qué aportó IA?** Apoyó análisis, implementación, pruebas y documentación. Los resultados de pruebas y análisis se obtuvieron ejecutando herramientas; la comprensión y sustentación final son responsabilidad de quien presenta.
