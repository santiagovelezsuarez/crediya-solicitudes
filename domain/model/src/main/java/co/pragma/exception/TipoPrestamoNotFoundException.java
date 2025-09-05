package co.pragma.exception;

public class TipoPrestamoNotFoundException extends BusinessException {
    public TipoPrestamoNotFoundException(String message) {
        super(ErrorCode.TIPO_PRESTAMO_NOT_FOUND, message);
    }
}
