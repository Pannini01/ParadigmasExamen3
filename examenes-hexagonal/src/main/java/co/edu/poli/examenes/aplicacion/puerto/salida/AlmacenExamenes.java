package co.edu.poli.examenes.aplicacion.puerto.salida;

import co.edu.poli.examenes.dominio.Examen;
import java.util.List;

/** Puerto de salida para instantáneas completas; no depende de archivos ni de JavaFX. */
public interface AlmacenExamenes {
    /**
 * @param examenes instantánea que debe persistirse
 * @throws PersistenciaException ante un fallo de escritura
 */
    void escribir(List<Examen> examenes);
    /**
 * @return instantánea leída
 * @throws PersistenciaException si no puede leerse o validarse
 */
    List<Examen> leer();
}
