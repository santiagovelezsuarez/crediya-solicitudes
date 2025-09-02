package co.pragma.usecase.solicitud;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreateSolicitudPrestamoInput {
    private String tipoDocumento;
    private String numeroDocumento;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private String tipoPrestamo;
}
