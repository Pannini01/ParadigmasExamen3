package co.edu.poli.examenes.aplicacion;
import static org.junit.jupiter.api.Assertions.*;
import static co.edu.poli.examenes.DatosPrueba.examen;
import co.edu.poli.examenes.aplicacion.puerto.salida.*;
import co.edu.poli.examenes.dominio.Examen;
import java.util.List;
import org.junit.jupiter.api.Test;

class ServicioExamenesTest {
    private final AlmacenFalso almacen = new AlmacenFalso();
    private final ServicioExamenes servicio = new ServicioExamenes(almacen);
    @Test void codigoDuplicadoNoSobrescribe() {
        Examen original = examen("A", "Ana");
        servicio.guardar(original);
        Examen repetido = examen(" a ", "Otro");
        assertThrows(IllegalArgumentException.class, () -> servicio.guardar(repetido));
        assertEquals(List.of(original), servicio.listar(OrdenExamen.CODIGO));
    }
    @Test void intercambiaEstrategiasSinModificarLaSesion() {
        Examen primero = examen("A", "Zulma");
        Examen segundo = examen("B", "Ana");
        servicio.guardar(segundo); servicio.guardar(primero);
        assertEquals(List.of(primero, segundo), servicio.listar(OrdenExamen.CODIGO));
        assertEquals(List.of(segundo, primero), servicio.listar(OrdenExamen.PACIENTE));
        List<Examen> copia = servicio.listar(OrdenExamen.CODIGO);
        assertThrows(UnsupportedOperationException.class, copia::clear);
        assertEquals(2, servicio.listar(OrdenExamen.CODIGO).size());
    }
    @Test void nombresIgualesDesempatanPorCodigo() {
        servicio.guardar(examen("B", "ANA")); servicio.guardar(examen("A", "ana"));
        assertEquals("A", servicio.listar(OrdenExamen.PACIENTE).getFirst().codigo());
    }
    @Test void serializarYRecuperarReemplazaLaSesion() {
        Examen guardado = examen("A", "Ana");
        servicio.guardar(guardado); servicio.serializar(); servicio.guardar(examen("B", "Beto"));
        servicio.deserializar();
        assertEquals(List.of(guardado), servicio.listar(OrdenExamen.CODIGO));
    }
    @Test void restauracionDuplicadaNoDejaCambiosParciales() {
        Examen original = examen("ORIGINAL", "Ana"); servicio.guardar(original);
        almacen.datos = List.of(examen("B", "Beto"), examen("b", "Otro"));
        assertThrows(IllegalArgumentException.class, servicio::deserializar);
        assertEquals(List.of(original), servicio.listar(OrdenExamen.CODIGO));
    }
    @Test void falloDeLecturaConservaSesion() {
        Examen original = examen("A", "Ana"); servicio.guardar(original); almacen.fallar = true;
        assertThrows(PersistenciaException.class, servicio::deserializar);
        assertEquals(List.of(original), servicio.listar(OrdenExamen.CODIGO));
    }
    @Test void falloDeEscrituraEsVisibleYConservaSesion() {
        servicio.guardar(examen("A", "Ana")); almacen.fallar = true;
        assertThrows(PersistenciaException.class, servicio::serializar);
        assertEquals(1, servicio.listar(OrdenExamen.CODIGO).size());
    }
    @Test void admiteInstantaneasVacias() {
        servicio.serializar(); servicio.guardar(examen("A", "Ana")); servicio.deserializar();
        assertTrue(servicio.listar(OrdenExamen.CODIGO).isEmpty());
    }
    private static final class AlmacenFalso implements AlmacenExamenes {
        List<Examen> datos = List.of(); boolean fallar;
        private void verificar() { if (fallar) { throw new PersistenciaException("Fallo simulado", null); } }
        public void escribir(List<Examen> examenes) { verificar(); datos = List.copyOf(examenes); }
        public List<Examen> leer() { verificar(); return datos; }
    }
}
