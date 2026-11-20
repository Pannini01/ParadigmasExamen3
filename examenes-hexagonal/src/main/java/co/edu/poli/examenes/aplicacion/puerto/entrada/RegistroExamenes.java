package co.edu.poli.examenes.aplicacion.puerto.entrada;

import co.edu.poli.examenes.dominio.Examen;
import java.util.Comparator;
import java.util.List;

/** Puerto de entrada independiente de JavaFX para las operaciones del registro. */
public interface RegistroExamenes {
    /**
 * Agrega a la sesión; rechaza un código existente sin modificarlo.
 * @param examen examen válido
 */
    void guardar(Examen examen);
    /**
 * @param orden estrategia de ordenación
 * @return copia inmutable ordenada
 */
    List<Examen> listar(Comparator<Examen> orden);
    /** Guarda la sesión completa en el almacenamiento configurado. */
    void serializar();
    /** Reemplaza la sesión solo después de leer y validar la totalidad del archivo. */
    void deserializar();
}
