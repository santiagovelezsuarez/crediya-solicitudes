package co.pragma.model.estadosolicitud;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EstadoSolicitudTest {

    @Test
    void shouldBuildEstadoSolicitudCorrectly() {
        EstadoSolicitud estado = EstadoSolicitud.builder()
                .id(1)
                .nombre("PENDIENTE")
                .descripcion("Solicitud en espera de aprobación")
                .build();

        assertEquals(1, estado.getId());
        assertEquals("PENDIENTE", estado.getNombre());
        assertEquals("Solicitud en espera de aprobación", estado.getDescripcion());
    }
}
