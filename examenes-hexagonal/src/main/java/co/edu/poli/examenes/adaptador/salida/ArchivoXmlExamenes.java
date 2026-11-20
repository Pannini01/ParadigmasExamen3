package co.edu.poli.examenes.adaptador.salida;

import co.edu.poli.examenes.aplicacion.puerto.salida.AlmacenExamenes;
import co.edu.poli.examenes.aplicacion.puerto.salida.PersistenciaException;
import co.edu.poli.examenes.dominio.Examen;
import co.edu.poli.examenes.dominio.FactorRh;
import co.edu.poli.examenes.dominio.TipoSangre;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/** Adaptador GoF de AlmacenExamenes a Properties XML y NIO; formato versionado sin objetos Java serializados. */
public final class ArchivoXmlExamenes implements AlmacenExamenes {
    private static final String VERSION = "1";
    private static final int MAX_REGISTROS = 10_000;
    private final Path archivo;

    /**
 * @param archivo ubicación de la instantánea; se crean sus directorios al escribir
 */
    public ArchivoXmlExamenes(Path archivo) { this.archivo = Objects.requireNonNull(archivo).toAbsolutePath(); }

    /**
 * {@inheritDoc}
 */
    @Override public void escribir(List<Examen> examenes) {
        if (examenes.size() > MAX_REGISTROS) {
            throw new PersistenciaException("El archivo admite hasta 10000 exámenes.", null);
        }
        Properties datos = new Properties();
        datos.setProperty("version", VERSION);
        datos.setProperty("cantidad", Integer.toString(examenes.size()));
        for (int i = 0; i < examenes.size(); i++) {
            codificar(datos, i + ".", examenes.get(i));
        }
        try {
            Files.createDirectories(archivo.getParent());
            guardarTemporal(datos);
        } catch (IOException ex) {
            throw new PersistenciaException("No se pudo guardar el archivo de exámenes.", ex);
        }
    }

    private static void codificar(Properties datos, String prefijo, Examen examen) {
        datos.setProperty(prefijo + "codigo", examen.codigo());
        datos.setProperty(prefijo + "paciente", examen.paciente());
        datos.setProperty(prefijo + "costo", examen.costo().toPlainString());
        datos.setProperty(prefijo + "tipo", examen.tipoSangre().name());
        datos.setProperty(prefijo + "rh", examen.factorRh().name());
    }

    private void guardarTemporal(Properties datos) throws IOException {
        Path temporal = Files.createTempFile(archivo.getParent(), "examenes-", ".tmp");
        try {
            try (OutputStream salida = Files.newOutputStream(temporal)) {
                datos.storeToXML(salida, "Exámenes de sangre - formato 1", "UTF-8");
            }
            try {
                Files.move(temporal, archivo, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException ex) {
                Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporal);
        }
    }

    /**
 * {@inheritDoc}
 */
    @Override public List<Examen> leer() {
        try (InputStream entrada = Files.newInputStream(archivo)) {
            Properties datos = new Properties();
            datos.loadFromXML(entrada);
            return decodificar(datos);
        } catch (IOException | IllegalArgumentException ex) {
            throw new PersistenciaException("No se pudo cargar el archivo. Verifique su existencia y formato.", ex);
        }
    }

    private static List<Examen> decodificar(Properties datos) {
        if (!VERSION.equals(datos.getProperty("version"))) {
            throw new IllegalArgumentException("Versión de archivo incompatible.");
        }
        int cantidad = Integer.parseInt(requerido(datos, "cantidad"));
        if (cantidad < 0 || cantidad > MAX_REGISTROS || datos.size() != 2 + cantidad * 5) {
            throw new IllegalArgumentException("Cantidad de registros o campos inconsistente.");
        }
        List<Examen> resultado = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            String prefijo = i + ".";
            resultado.add(Examen.builder()
                    .codigo(requerido(datos, prefijo + "codigo"))
                    .paciente(requerido(datos, prefijo + "paciente"))
                    .costo(new BigDecimal(requerido(datos, prefijo + "costo")))
                    .tipoSangre(TipoSangre.valueOf(requerido(datos, prefijo + "tipo")))
                    .factorRh(FactorRh.valueOf(requerido(datos, prefijo + "rh"))).build());
        }
        return List.copyOf(resultado);
    }

    private static String requerido(Properties datos, String clave) {
        String valor = datos.getProperty(clave);
        if (valor == null) { throw new IllegalArgumentException("Falta el campo " + clave + "."); }
        return valor;
    }
}
