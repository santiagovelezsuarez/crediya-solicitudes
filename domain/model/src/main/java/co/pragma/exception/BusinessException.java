package co.pragma.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode code;

    protected BusinessException(ErrorCode code, String message){
        super(message);
        this.code = code;
    }
}
