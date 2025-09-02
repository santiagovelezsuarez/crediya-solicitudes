package co.pragma.exception;

public class ClienteNotFoundException extends BusinessException {
    public ClienteNotFoundException(String message) {
        super(ErrorCode.CLIENTE_NOT_FOUND, message);
    }
}
