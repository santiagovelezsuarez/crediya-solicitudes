package co.pragma.model.estadosolicitud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadoSolicitudTest {

    @Test
    void shouldBuildEstadoSolicitudCorrectly() {
        EstadoSolicitud estado = EstadoSolicitud.builder()
                .id(1)
                .nombre(EstadoSolicitudCodigo.PENDIENTE_REVISION)
                .descripcion("Solicitud en espera de aprobación")
                .build();

        assertEquals(1, estado.getId());
        assertEquals("PENDIENTE_REVISION", estado.getNombre().name());
        assertEquals("Solicitud en espera de aprobación", estado.getDescripcion());
    }
}
