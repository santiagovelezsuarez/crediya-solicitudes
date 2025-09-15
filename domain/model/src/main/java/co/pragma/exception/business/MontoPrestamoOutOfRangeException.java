package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

import java.math.BigDecimal;

public class MontoPrestamoOutOfRangeException extends BusinessException {
    public MontoPrestamoOutOfRangeException(BigDecimal monto, BigDecimal minimo, BigDecimal maximo) {
        super(
                ErrorCode.TIPO_PRESTAMO_OUT_RANGE,
                String.format(ErrorCode.TIPO_PRESTAMO_OUT_RANGE.getDefaultMessage(), monto, minimo, maximo)
        );
    }
}
