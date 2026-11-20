package co.edu.poli.examenes.aplicacion;

import co.edu.poli.examenes.dominio.Examen;
import java.util.Comparator;

/** Estrategias GoF intercambiables; Comparator es el contrato de la estrategia. */
public enum OrdenExamen implements Comparator<Examen> {
    /** Orden lexicográfico de códigos normalizados. */
    CODIGO("Código", Comparator.comparing(Examen::codigo)),
    /** Orden por nombre sin distinguir mayúsculas; desempata por código. */
    PACIENTE("Paciente", Comparator.comparing(Examen::paciente, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(Examen::codigo));

    private final String etiqueta;
    private final Comparator<Examen> comparador;

    OrdenExamen(String etiqueta, Comparator<Examen> comparador) {
        this.etiqueta = etiqueta;
        this.comparador = comparador;
    }
    /**
 * {@inheritDoc}
 */
    @Override public int compare(Examen primero, Examen segundo) { return comparador.compare(primero, segundo); }
    /**
 * @return etiqueta que se muestra al seleccionar la estrategia
 */
    @Override public String toString() { return etiqueta; }
}
