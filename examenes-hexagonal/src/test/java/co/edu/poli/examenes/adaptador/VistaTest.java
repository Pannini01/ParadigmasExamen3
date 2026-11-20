package co.edu.poli.examenes.adaptador;
import static org.junit.jupiter.api.Assertions.*;
import co.edu.poli.examenes.adaptador.entrada.ControladorExamenes;
import co.edu.poli.examenes.adaptador.salida.ArchivoXmlExamenes;
import co.edu.poli.examenes.aplicacion.*;
import co.edu.poli.examenes.bootstrap.App;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.scene.image.WritableImage;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

class VistaTest {
    @TempDir Path temporal;
    @BeforeAll static void iniciarJavaFx() throws Exception {
        CompletableFuture<Void> inicio = new CompletableFuture<>();
        Platform.startup(() -> { Platform.setImplicitExit(false); inicio.complete(null); });
        inicio.get(20, TimeUnit.SECONDS);
    }
    @Test void botonesEjecutanFlujoCompletoYValidaciones() throws Exception {
        enFx(() -> {
            ServicioExamenes servicio = new ServicioExamenes(new ArchivoXmlExamenes(temporal.resolve("examenes.xml")));
            FXMLLoader loader = new FXMLLoader(App.class.getResource("/vista/examenes.fxml"));
            loader.setControllerFactory(tipo -> new ControladorExamenes(servicio, OrdenExamen.PACIENTE));
            Parent vista = loader.load();
            TextArea salida = nodo(vista, "txtAreaMensajes", TextArea.class);
            pulsar(vista, "btnListar"); assertTrue(salida.getText().contains("No hay"));
            pulsar(vista, "btnGuardar"); assertTrue(salida.getText().startsWith("Error:"));
            nodo(vista, "txtCodigoExamen", TextField.class).setText("EX-01");
            nodo(vista, "txtNombrePaciente", TextField.class).setText("Ana Pérez");
            nodo(vista, "txtCosto", TextField.class).setText("100,50");
            combo(vista, "cmbTipoSangre").setValue("O");
            nodo(vista, "rbNegativo", RadioButton.class).setSelected(true);
            pulsar(vista, "btnGuardar"); assertTrue(salida.getText().contains("guardado"));
            pulsar(vista, "btnGuardar"); assertTrue(salida.getText().contains("Ya existe"));
            pulsar(vista, "btnSerializar"); assertTrue(salida.getText().contains("correctamente"));
            assertNull(vista.lookup("#btnLimpiar"));
            assertNull(vista.lookup("#cmbOrden"));
            pulsar(vista, "btnListar"); assertTrue(salida.getText().contains("EX-01 | Ana Pérez | $100.50 | O NEGATIVO"));
            nodo(vista, "txtCodigoExamen", TextField.class).setText("EX-00");
            nodo(vista, "txtNombrePaciente", TextField.class).setText("Zulma López");
            pulsar(vista, "btnGuardar"); pulsar(vista, "btnListar");
            assertTrue(salida.getText().indexOf("Ana Pérez") < salida.getText().indexOf("Zulma López"));
            pulsar(vista, "btnDeserializar"); assertTrue(salida.getText().startsWith("Sesión recuperada."));
            Files.writeString(temporal.resolve("examenes.xml"), "corrupto");
            pulsar(vista, "btnDeserializar"); assertTrue(salida.getText().startsWith("Error:"));
            assertEquals(1, servicio.listar(OrdenExamen.CODIGO).size());
            return null;
        });
    }
    @Test void puntoDeComposicionAbreVentanaReal() throws Exception {
        enFx(() -> {
            Stage stage = new Stage();
            try {
                new App().start(stage);
                assertTrue(stage.isShowing()); assertEquals("Exámenes de Sangre", stage.getTitle());
                assertEquals(640.0, stage.getScene().getWidth());
                assertEquals(580.0, stage.getScene().getHeight());
                assertFalse(stage.isResizable());
                Parent vista = stage.getScene().getRoot();
                nodo(vista, "txtCodigoExamen", TextField.class).setText("EXM-001");
                nodo(vista, "txtNombrePaciente", TextField.class).setText("Paciente de demostración");
                nodo(vista, "txtCosto", TextField.class).setText("15000,50");
                combo(vista, "cmbTipoSangre").setValue("AB");
                pulsar(vista, "btnGuardar"); pulsar(vista, "btnListar");
                capturar(vista);
            }
            finally { stage.close(); }
            return null;
        });
    }
    @Test void conservaFXMLOriginalSalvoReferenciaDelControlador() throws Exception {
        try (var entrada = App.class.getResourceAsStream("/vista/examenes.fxml")) {
            assertNotNull(entrada);
            String fxml = new String(entrada.readAllBytes(), StandardCharsets.UTF_8)
                    .replace("co.edu.poli.examenes.adaptador.entrada.ControladorExamenes", "co.edu.poli.Examen2.controlador.PrimaryController");
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(fxml.getBytes(StandardCharsets.UTF_8));
            assertEquals("7f3bfa59568dd7f5aba0becd9352187ba801e2ced566b2778bd193cad4946d35", HexFormat.of().formatHex(hash));
        }
    }
    private static void capturar(Parent vista) throws Exception {
        WritableImage imagen = vista.snapshot(null, null);
        BufferedImage salida = new BufferedImage((int) imagen.getWidth(), (int) imagen.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < salida.getHeight(); y++) {
            for (int x = 0; x < salida.getWidth(); x++) {
                salida.setRGB(x, y, imagen.getPixelReader().getArgb(x, y));
            }
        }
        Path destino = Path.of("target/evidencias/aplicacion.png");
        Files.createDirectories(destino.getParent()); ImageIO.write(salida, "png", destino.toFile());
    }
    private static <T> T nodo(Parent vista, String id, Class<T> clase) { return clase.cast(vista.lookup("#" + id)); }
    @SuppressWarnings("unchecked")
    private static ComboBox<Object> combo(Parent vista, String id) { return nodo(vista, id, ComboBox.class); }
    private static void pulsar(Parent vista, String id) { nodo(vista, id, Button.class).fire(); }
    private static void enFx(Callable<Void> tarea) throws Exception {
        FutureTask<Void> futuro = new FutureTask<>(tarea); Platform.runLater(futuro); futuro.get(20, TimeUnit.SECONDS);
    }
}
