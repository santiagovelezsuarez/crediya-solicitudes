package co.pragma.model.cliente;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteTest {

    @Test
    void builderCreatesCorrectCliente() {
        Cliente cliente = Cliente.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .documentoIdentidad(new DocumentoIdentidadVO("CC", "123456789"))
                .email("pepe@mail.co")
                .build();

        assertEquals("123e4567-e89b-12d3-a456-426614174000", cliente.getId().toString());
        assertEquals("CC", cliente.getDocumentoIdentidad().tipoDocumento());
        assertEquals("123456789", cliente.getDocumentoIdentidad().numeroDocumento());
        assertEquals("pepe@mail.co", cliente.getEmail());
    }
}
