package co.pragma.model.cliente;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteTest {

    @Test
    void builderCreatesCorrectCliente() {
        Cliente cliente = Cliente.builder()
                .id(UUID.fromString("123e4567-e89b-12d3-a456-426614174000"))
                .email("pepe@mail.co")
                .build();

        assertEquals("123e4567-e89b-12d3-a456-426614174000", cliente.getId().toString());
        assertEquals("pepe@mail.co", cliente.getEmail());
    }
}
