package co.pragma.model.estadosolicitud;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EstadoSolicitudCodigo {
    PENDIENTE_REVISION((short) 1),
    RECHAZADA((short) 2),
    REVISION_MANUAL((short) 3);

    private final short code;

    public static EstadoSolicitudCodigo valueOf(short code) {
        return Arrays.stream(values())
                .filter(estado -> estado.getCode() == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado inválido: " + code));
    }
}
