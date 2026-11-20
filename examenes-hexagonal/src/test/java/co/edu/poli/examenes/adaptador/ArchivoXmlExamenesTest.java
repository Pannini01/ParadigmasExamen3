package co.edu.poli.examenes.adaptador;
import static org.junit.jupiter.api.Assertions.*;
import static co.edu.poli.examenes.DatosPrueba.examen;
import co.edu.poli.examenes.adaptador.salida.ArchivoXmlExamenes;
import co.edu.poli.examenes.aplicacion.puerto.salida.PersistenciaException;
import co.edu.poli.examenes.dominio.Examen;
import java.io.OutputStream;
import java.nio.file.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class ArchivoXmlExamenesTest {
    @TempDir Path temporal;
    private Path ruta() { return temporal.resolve("datos/examenes.xml"); }
    private ArchivoXmlExamenes almacen;
    @BeforeEach void prepararAlmacen() { almacen = new ArchivoXmlExamenes(ruta()); }
    @Test void roundTripConUnicodeYCaracteresXml() throws Exception {
        List<Examen> datos = List.of(examen("X&1", "María <López>"), examen("B", "李 Pérez"));
        almacen.escribir(datos);
        assertEquals(datos, new ArchivoXmlExamenes(ruta()).leer());
        assertTrue(Files.readString(ruta()).contains("version"));
    }
    @Test void sobrescribeSinDejarTemporales() throws Exception {
        almacen.escribir(List.of(examen("A", "Ana"))); almacen.escribir(List.of());
        assertTrue(almacen.leer().isEmpty());
        try (var archivos = Files.list(ruta().getParent())) { assertEquals(1, archivos.count()); }
    }
    @Test void archivoInexistenteProduceErrorExplicito() {
        assertThrows(PersistenciaException.class, almacen::leer);
    }
    @Test void archivoCorruptoProduceErrorExplicito() throws Exception {
        Files.createDirectories(ruta().getParent()); Files.writeString(ruta(), "archivo corrupto");
        assertThrows(PersistenciaException.class, almacen::leer);
    }
    @ParameterizedTest @CsvSource({"version,2", "cantidad,-1", "cantidad,10001", "cantidad,abc", "cantidad,2", "0.costo,-1", "0.tipo,X", "0.rh,X", "0.codigo,' '"})
    void rechazaMetadatosYRegistrosInvalidos(String clave, String valor) throws Exception {
        almacen.escribir(List.of(examen("A", "Ana")));
        Properties datos = cargar(); datos.setProperty(clave, valor); escribir(datos);
        assertThrows(PersistenciaException.class, almacen::leer);
    }
    @Test void rechazaCampoObligatorioAusenteAunqueLaCantidadCoincida() throws Exception {
        almacen.escribir(List.of(examen("A", "Ana")));
        Properties datos = cargar(); datos.remove("0.paciente"); datos.setProperty("extra", "valor"); escribir(datos);
        assertThrows(PersistenciaException.class, almacen::leer);
    }
    @Test void rechazaExcesoDeRegistrosAntesDeEscribir() {
        List<Examen> muchos = Collections.nCopies(10001, examen("A", "Ana"));
        assertThrows(PersistenciaException.class, () -> almacen.escribir(muchos));
        assertFalse(Files.exists(ruta()));
    }
    @Test void errorDeEscrituraNoSeOculta() throws Exception {
        Files.writeString(temporal.resolve("datos"), "No es un directorio");
        List<Examen> vacia = List.of();
        assertThrows(PersistenciaException.class, () -> almacen.escribir(vacia));
    }
    private Properties cargar() throws Exception {
        Properties datos = new Properties();
        try (var entrada = Files.newInputStream(ruta())) { datos.loadFromXML(entrada); }
        return datos;
    }
    private void escribir(Properties datos) throws Exception {
        try (OutputStream salida = Files.newOutputStream(ruta())) { datos.storeToXML(salida, "Prueba"); }
    }
}
