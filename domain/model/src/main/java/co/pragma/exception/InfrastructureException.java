package co.pragma.exception;

public class InfrastructureException extends RuntimeException {
    public InfrastructureException(String code, Throwable cause) {
        super(code, cause);
    }
}
