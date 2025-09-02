package co.pragma.exception;

public record DomainError(String code, String message) {
    public static DomainError from(BusinessException ex) {
        return new DomainError(ex.getCode().name(), ex.getMessage());
    }
}
