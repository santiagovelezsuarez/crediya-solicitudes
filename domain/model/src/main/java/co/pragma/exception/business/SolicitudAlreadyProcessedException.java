package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class SolicitudAlreadyProcessedException extends  BusinessException {
    public SolicitudAlreadyProcessedException(String estado) {
        super(ErrorCode.SOLICITUD_PRESTAMO_PROCESSED,  String.format(ErrorCode.SOLICITUD_PRESTAMO_PROCESSED.getDefaultMessage(), estado));
    }
}
