package co.pragma.exception.business;

import co.pragma.error.ErrorCode;

public class ClienteNotFoundException extends BusinessException {
    public ClienteNotFoundException() {
        super(ErrorCode.CLIENTE_NOT_FOUND);
    }
}
