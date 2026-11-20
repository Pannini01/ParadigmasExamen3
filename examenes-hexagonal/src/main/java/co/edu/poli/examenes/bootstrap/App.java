package co.edu.poli.examenes.bootstrap;

import co.edu.poli.examenes.adaptador.entrada.ControladorExamenes;
import co.edu.poli.examenes.adaptador.salida.ArchivoXmlExamenes;
import co.edu.poli.examenes.aplicacion.ServicioExamenes;
import co.edu.poli.examenes.aplicacion.OrdenExamen;
import java.util.Locale;
import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Punto de composición: único lugar que conoce implementaciones de entrada y salida. */
public final class App extends Application {
    /**
 * Construye las dependencias y muestra la ventana.
 * @param stage ventana principal
 * @throws IOException si falta la vista
 */
    @Override public void start(Stage stage) throws IOException {
        Path archivo = Path.of(System.getProperty("examenes.archivo", "datos/examenes.xml"));
        ServicioExamenes servicio = new ServicioExamenes(new ArchivoXmlExamenes(archivo));
        FXMLLoader loader = new FXMLLoader(App.class.getResource("/vista/examenes.fxml"));
        OrdenExamen orden = OrdenExamen.valueOf(System.getProperty("examenes.orden", "CODIGO").toUpperCase(Locale.ROOT));
        loader.setControllerFactory(tipo -> new ControladorExamenes(servicio, orden));
        stage.setScene(new Scene(loader.load(), 640, 580));
        stage.setTitle("Exámenes de Sangre");
        stage.setResizable(false);
        stage.show();
    }
    /**
 * @param args argumentos de JavaFX
 */
    public static void main(String[] args) { launch(args); }
}
