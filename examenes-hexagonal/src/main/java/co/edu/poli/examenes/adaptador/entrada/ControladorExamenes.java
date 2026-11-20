package co.edu.poli.examenes.adaptador.entrada;

import co.edu.poli.examenes.aplicacion.puerto.entrada.RegistroExamenes;
import co.edu.poli.examenes.aplicacion.puerto.salida.PersistenciaException;
import co.edu.poli.examenes.dominio.Examen;
import co.edu.poli.examenes.dominio.FactorRh;
import co.edu.poli.examenes.dominio.TipoSangre;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Comparator;
import java.util.stream.Collectors;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/** Adaptador de entrada: convierte datos de pantalla y delega al puerto de aplicación. */
public final class ControladorExamenes {
    private final RegistroExamenes registro;
    private final Comparator<Examen> orden;
    @FXML private TextField txtCodigoExamen;
    @FXML private TextField txtNombrePaciente;
    @FXML private TextField txtCosto;
    @FXML private ComboBox<String> cmbTipoSangre;
    @FXML private RadioButton rbPositivo;
    @FXML private TextArea txtAreaMensajes;

    /**
     * Conecta la vista original con los casos de uso y la estrategia configurada.
     * @param registro casos de uso sin dependencias de JavaFX
     * @param orden criterio de listado inyectado por App
     */
    public ControladorExamenes(RegistroExamenes registro, Comparator<Examen> orden) {
        this.registro = Objects.requireNonNull(registro);
        this.orden = Objects.requireNonNull(orden);
    }

    @FXML private void handleGuardar() {
        ejecutar(() -> {
            Examen examen = Examen.builder().codigo(txtCodigoExamen.getText())
                    .paciente(txtNombrePaciente.getText()).costo(costoIngresado())
                    .tipoSangre(tipoSeleccionado())
                    .factorRh(rbPositivo.isSelected() ? FactorRh.POSITIVO : FactorRh.NEGATIVO).build();
            registro.guardar(examen);
            txtAreaMensajes.setText("Examen " + examen.codigo() + " guardado en la sesión. Use Serializar para conservarlo al cerrar.");
        });
    }

    private BigDecimal costoIngresado() {
        String texto = txtCosto.getText().strip();
        if (!texto.matches("\\d+([.,]\\d{1,2})?")) {
            throw new IllegalArgumentException("Ingrese un costo no negativo, sin separador de miles y con hasta dos decimales.");
        }
        return new BigDecimal(texto.replace(',', '.'));
    }

    private TipoSangre tipoSeleccionado() {
        String seleccion = cmbTipoSangre.getValue();
        return seleccion == null ? null : TipoSangre.valueOf(seleccion);
    }

    @FXML private void handleSerializar() {
        ejecutar(() -> { registro.serializar(); txtAreaMensajes.setText("Sesión serializada correctamente en el archivo XML."); });
    }
    @FXML private void handleDeserializar() {
        ejecutar(() -> { registro.deserializar(); mostrarListado("Sesión recuperada.\n"); });
    }
    @FXML private void handleListar() { ejecutar(() -> mostrarListado("")); }

    private void mostrarListado(String prefijo) {
        String listado = registro.listar(orden).stream()
                .map(examen -> "%s | %s | $%s | %s %s".formatted(examen.codigo(), examen.paciente(),
                        examen.costo().toPlainString(), examen.tipoSangre(), examen.factorRh()))
                .collect(Collectors.joining("\n"));
        txtAreaMensajes.setText(prefijo + (listado.isEmpty() ? "No hay exámenes registrados." : listado));
    }

    private void ejecutar(Runnable accion) {
        try { accion.run(); }
        catch (IllegalArgumentException | PersistenciaException ex) { txtAreaMensajes.setText("Error: " + ex.getMessage()); }
    }
}
