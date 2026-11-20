package co.edu.poli.examenes.aplicacion.puerto.salida;

/** Fallo de infraestructura expresado sin filtrar excepciones del mecanismo de almacenamiento. */
public final class PersistenciaException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    /**
 * @param mensaje explicación apta para el usuario
 * @param causa detalle para el diagnóstico
 */
    public PersistenciaException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
