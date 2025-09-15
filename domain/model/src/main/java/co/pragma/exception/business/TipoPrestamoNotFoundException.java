package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

public class TipoPrestamoNotFoundException extends BusinessException {
    public TipoPrestamoNotFoundException() {
        super(ErrorCode.TIPO_PRESTAMO_NOT_FOUND);
    }
}
