package co.pragma.model.solicitudprestamo;
import co.pragma.model.estadosolicitud.EstadoSolicitudCodigo;
import lombok.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class SolicitudPrestamo {
    private UUID id;
    private String codigo;
    private UUID idCliente;
    private UUID idTipoPrestamo;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private EstadoSolicitudCodigo estado;
    private Boolean notificado;

    /**
     * Ref: HU7
     * Calcula la cuota mensual de un préstamo.
     * Fórmula de Cuota: P * (i * (1 + i)^n) / ((1 + i)^n - 1)
     * Donde:
     * - P: Monto del préstamo.
     * - i: Tasa de interés mensual.
     * - n: Plazo en meses.     *
     * @param tasaInteresAnual La tasa de interés anual en porcentaje.
     * @return La cuota mensual calculada con dos decimales.
     */
    public BigDecimal calcularCuota(BigDecimal tasaInteresAnual) {
        MathContext mc = new MathContext(20, RoundingMode.HALF_UP);

        BigDecimal tasaMensual = tasaInteresAnual.divide(BigDecimal.valueOf(100), mc).divide(BigDecimal.valueOf(12), mc);

        if (tasaMensual.compareTo(BigDecimal.ZERO) == 0)
            return monto.divide(BigDecimal.valueOf(plazoEnMeses), 2, RoundingMode.HALF_UP);

        BigDecimal unoMasTasa = BigDecimal.ONE.add(tasaMensual, mc);
        BigDecimal potencia = unoMasTasa.pow(-plazoEnMeses, mc);

        BigDecimal numerador = monto.multiply(tasaMensual, mc);
        BigDecimal denominador = BigDecimal.ONE.subtract(potencia, mc);

        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    public boolean esProcesable() {
        return estado == EstadoSolicitudCodigo.PENDIENTE_REVISION ||
               estado == EstadoSolicitudCodigo.REVISION_MANUAL;
    }
}
