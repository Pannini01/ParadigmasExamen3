package co.edu.poli.examenes.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Examen inmutable y válido. Builder permite expresar los cinco datos por nombre.
 * @param codigo identificador único, normalizado a mayúsculas
 * @param paciente nombre no vacío
 * @param costo importe no negativo con hasta dos decimales significativos
 * @param tipoSangre grupo registrado
 * @param factorRh factor registrado
 */
public record Examen(String codigo, String paciente, BigDecimal costo,
                     TipoSangre tipoSangre, FactorRh factorRh) {
    /** Valida también la construcción directa y la recuperación desde archivos. */
    public Examen {
        codigo = texto(codigo, "Código", 40).toUpperCase(Locale.ROOT);
        paciente = texto(paciente, "Paciente", 120);
        if (costo == null || costo.signum() < 0) {
            throw new IllegalArgumentException("El costo debe ser mayor o igual a cero.");
        }
        try {
            costo = costo.setScale(2, RoundingMode.UNNECESSARY);
        } catch (ArithmeticException ex) {
            throw new IllegalArgumentException("El costo admite hasta dos decimales.", ex);
        }
        if (tipoSangre == null || factorRh == null) {
            throw new IllegalArgumentException("Seleccione el grupo sanguíneo y el factor Rh.");
        }
    }

    private static String texto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank() || valor.strip().length() > maximo
                || valor.codePoints().anyMatch(Character::isISOControl)) {
            throw new IllegalArgumentException(campo + ": ingrese entre 1 y " + maximo + " caracteres sin controles.");
        }
        return valor.strip();
    }

    /**
 * @return un constructor independiente, sin estado compartido
 */
    public static Builder builder() { return new Builder(); }

    /** Constructor gradual GoF; build delega todas las invariantes al producto. */
    public static final class Builder {
        private String codigo;
        private String paciente;
        private BigDecimal costo;
        private TipoSangre tipoSangre;
        private FactorRh factorRh;

        private Builder() { }
        /**
 * @param valor código del examen
 * @return este constructor
 */
        public Builder codigo(String valor) { codigo = valor; return this; }
        /**
 * @param valor nombre del paciente
 * @return este constructor
 */
        public Builder paciente(String valor) { paciente = valor; return this; }
        /**
 * @param valor costo monetario
 * @return este constructor
 */
        public Builder costo(BigDecimal valor) { costo = valor; return this; }
        /**
 * @param valor grupo sanguíneo
 * @return este constructor
 */
        public Builder tipoSangre(TipoSangre valor) { tipoSangre = valor; return this; }
        /**
 * @param valor factor Rh
 * @return este constructor
 */
        public Builder factorRh(FactorRh valor) { factorRh = valor; return this; }
        /**
 * @return un examen válido e inmutable
 * @throws IllegalArgumentException si falta un dato o es inválido
 */
        public Examen build() { return new Examen(codigo, paciente, costo, tipoSangre, factorRh); }
    }
}
