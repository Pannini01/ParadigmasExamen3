package co.edu.poli.examenes.arquitectura;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArquitecturaTest {
    @Test void nucleoNoImportaAdaptadoresNiInfraestructura() throws Exception {
        Path base = Path.of("src/main/java/co/edu/poli/examenes");
        for (String capa : List.of("dominio", "aplicacion")) {
            try (var archivos = Files.walk(base.resolve(capa))) {
                for (Path archivo : archivos.filter(p -> p.toString().endsWith(".java")).toList()) {
                    String fuente = Files.readString(archivo);
                    for (String prohibido : List.of("import javafx.", "import java.nio.file.", "import java.sql.",
                            "import co.edu.poli.examenes.adaptador.", "import co.edu.poli.examenes.bootstrap.")) {
                        assertFalse(fuente.contains(prohibido), archivo + " depende de " + prohibido);
                    }
                    if (capa.equals("dominio")) {
                        assertFalse(fuente.contains("import co.edu.poli.examenes.aplicacion."), "Dominio no depende de aplicación");
                    }
                }
            }
        }
    }
}
