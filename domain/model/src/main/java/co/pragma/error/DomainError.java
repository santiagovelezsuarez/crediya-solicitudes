package co.pragma.error;

import co.pragma.exception.business.BusinessException;

public record DomainError(String code, String message) {
    public static DomainError from(BusinessException ex) {
        return new DomainError(ex.getCode().name(), ex.getMessage());
    }
}
