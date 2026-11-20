package co.edu.poli.examenes.aplicacion;

import co.edu.poli.examenes.aplicacion.puerto.entrada.RegistroExamenes;
import co.edu.poli.examenes.aplicacion.puerto.salida.AlmacenExamenes;
import co.edu.poli.examenes.dominio.Examen;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Casos de uso y sesión de trabajo. El cliente debe invocarlos desde un único hilo. */
public final class ServicioExamenes implements RegistroExamenes {
    private final AlmacenExamenes almacen;
    private Map<String, Examen> sesion = new LinkedHashMap<>();

    /**
 * @param almacen puerto de persistencia inyectado por el punto de composición
 */
    public ServicioExamenes(AlmacenExamenes almacen) { this.almacen = Objects.requireNonNull(almacen); }

    /**
 * {@inheritDoc}
 */
    @Override public void guardar(Examen examen) { agregar(sesion, examen); }

    private static void agregar(Map<String, Examen> destino, Examen examen) {
        Objects.requireNonNull(examen, "El examen es obligatorio.");
        if (destino.putIfAbsent(examen.codigo(), examen) != null) {
            throw new IllegalArgumentException("Ya existe el código " + examen.codigo() + ".");
        }
    }
    /**
 * {@inheritDoc}
 */
    @Override public List<Examen> listar(Comparator<Examen> orden) {
        return sesion.values().stream().sorted(Objects.requireNonNull(orden)).toList();
    }
    /**
 * {@inheritDoc}
 */
    @Override public void serializar() { almacen.escribir(List.copyOf(sesion.values())); }
    /**
 * {@inheritDoc}
 */
    @Override public void deserializar() {
        Map<String, Examen> nueva = new LinkedHashMap<>();
        almacen.leer().forEach(examen -> agregar(nueva, examen));
        sesion = nueva;
    }
}
