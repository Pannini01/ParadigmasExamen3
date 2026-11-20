package co.edu.poli.examenes.dominio;
import static org.junit.jupiter.api.Assertions.*;
import static co.edu.poli.examenes.DatosPrueba.examen;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class ExamenTest {
    @Test void normalizaCodigoYConservaImporteExacto() {
        Examen e = examen("  ex-01 ", "  María López  ");
        assertAll(() -> assertEquals("EX-01", e.codigo()), () -> assertEquals("María López", e.paciente()),
                () -> assertEquals(new BigDecimal("15000.50"), e.costo()),
                () -> assertEquals(TipoSangre.AB, e.tipoSangre()), () -> assertEquals(FactorRh.NEGATIVO, e.factorRh()));
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings={" ", "A\nB"})
    void rechazaTextoInvalido(String texto) {
        assertThrows(IllegalArgumentException.class, () -> examen(texto, "Ana"));
        assertThrows(IllegalArgumentException.class, () -> examen("A", texto));
    }
    @Test void respetaLongitudDeCampos() {
        assertDoesNotThrow(() -> examen("A".repeat(40), "N".repeat(120)));
        String codigoLargo = "A".repeat(41);
        assertThrows(IllegalArgumentException.class, () -> examen(codigoLargo, "Ana"));
        String nombreLargo = "N".repeat(121);
        assertThrows(IllegalArgumentException.class, () -> examen("A", nombreLargo));
    }
    @ParameterizedTest @ValueSource(strings={"-1", "1.001"})
    void rechazaCostoInvalido(String costo) {
        BigDecimal importe = new BigDecimal(costo);
        assertThrows(IllegalArgumentException.class, () -> new Examen("A", "Ana", importe, TipoSangre.O, FactorRh.POSITIVO));
    }
    @Test void aceptaCeroYDecimalesNoSignificativos() {
        Examen e = new Examen("A", "Ana", new BigDecimal("0.000"), TipoSangre.O, FactorRh.POSITIVO);
        assertEquals(new BigDecimal("0.00"), e.costo());
    }
    @Test void exigeTodosLosDatosTambienSinBuilder() {
        Examen.Builder vacio = Examen.builder();
        assertAll(
            () -> assertThrows(IllegalArgumentException.class, vacio::build),
            () -> assertThrows(IllegalArgumentException.class, () -> new Examen("A", "Ana", null, TipoSangre.A, FactorRh.POSITIVO)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Examen("A", "Ana", BigDecimal.ONE, null, FactorRh.POSITIVO)),
            () -> assertThrows(IllegalArgumentException.class, () -> new Examen("A", "Ana", BigDecimal.ONE, TipoSangre.A, null)));
    }
}
