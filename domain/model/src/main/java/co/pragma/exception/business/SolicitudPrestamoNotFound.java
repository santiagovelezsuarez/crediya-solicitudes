package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class SolicitudPrestamoNotFound extends BusinessException {
    public  SolicitudPrestamoNotFound() {
        super(ErrorCode.SOLICITUD_PRESTAMO_NOT_FOUND);
    }
}
