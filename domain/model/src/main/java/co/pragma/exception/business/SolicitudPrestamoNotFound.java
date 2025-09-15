package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

public class SolicitudPrestamoNotFound extends BusinessException {
    public  SolicitudPrestamoNotFound() {
        super(ErrorCode.SOLICITUD_PRESTAMO_NOT_FOUND);
    }
}
