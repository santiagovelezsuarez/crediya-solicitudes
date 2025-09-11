package co.pragma.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    CLIENTE_NOT_FOUND("Cliente no encontrado."),
    TIPO_PRESTAMO_NOT_FOUND("El tipo de prestamo especificado no existe."),
    TIPO_PRESTAMO_OUT_RANGE("El monto del préstamo seleccionado %.2f debe estar entre %.2f y %.2f"),
    ROL_NOT_FOUND("El rol especificado no existe."),
    FORBIDDEN("No tiene permisos para realizar esta acción."),
    TECHNICAL_ERROR("Ocurrió un error técnico, intente más tarde."),
    INVALID_INPUT("Existen errores de validación."),
    INVALID_REQUEST("La solicitud es inválida o malformada."),
    INTERNAL_SERVER_ERROR("Ocurrió un error interno, por favor intente más tarde."),
    DB_ERROR("Error de base de datos");

    private final String defaultMessage;

    ErrorCode(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }
}
