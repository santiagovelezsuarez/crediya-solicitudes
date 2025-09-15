package co.pragma.exception.business;

import co.pragma.exception.ErrorCode;

public class RolNotFoundException extends BusinessException {
    public RolNotFoundException() {
        super(ErrorCode.ROL_NOT_FOUND);
    }
}
