package co.edu.poli.examenes;
import co.edu.poli.examenes.dominio.*;
import java.math.BigDecimal;
public final class DatosPrueba {
    private DatosPrueba() { }
    public static Examen examen(String codigo, String paciente) {
        return Examen.builder().codigo(codigo).paciente(paciente).costo(new BigDecimal("15000.50"))
                .tipoSangre(TipoSangre.AB).factorRh(FactorRh.NEGATIVO).build();
    }
}
