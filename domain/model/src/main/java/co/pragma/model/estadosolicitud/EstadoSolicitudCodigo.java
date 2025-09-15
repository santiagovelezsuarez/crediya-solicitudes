package co.pragma.model.estadosolicitud;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum EstadoSolicitudCodigo {
    PENDIENTE_REVISION(1),
    REVISION_MANUAL(2),
    APROBADA(3),
    RECHAZADA(4);

    private final int code;

    public static EstadoSolicitudCodigo fromCode(int code) {
        return Arrays.stream(values())
                .filter(e -> e.code == code)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Estado inválido: " + code));
    }

    public static EstadoSolicitudCodigo fromCode(Integer code) {
        if (code == null) return null;
        return fromCode(code.intValue());
    }

    public int toCode() {
        return this.code;
    }

}
