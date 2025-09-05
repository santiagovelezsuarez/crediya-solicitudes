package co.pragma.usecase.solicitud.validators;

import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.cliente.gateways.UsuariosPort;
import co.pragma.usecase.solicitud.businessrules.ClienteValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import java.util.UUID;
import static org.mockito.Mockito.when;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ClienteValidatorTest {

    @Mock
    private UsuariosPort usuariosPort;

    @Test
    void shouldFailWhenClienteDoesNotExist() {
        DocumentoIdentidadVO documento = new DocumentoIdentidadVO("CC", "123456789");

        ClienteValidator validator = new ClienteValidator(usuariosPort);

        when(usuariosPort.findClienteByDocumento(documento))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate(documento))
                .expectErrorMatches(ex -> ex instanceof RuntimeException &&
                        ex.getMessage().equals("El cliente no está registrado"))
                .verify();
    }

    @Test
    void shouldPassWhenClienteExists() {
        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .documentoIdentidad(new DocumentoIdentidadVO("CC", "123456789"))
                .email("email")
                .build();

        ClienteValidator validator = new ClienteValidator(usuariosPort);

        when(usuariosPort.findClienteByDocumento(cliente.getDocumentoIdentidad()))
                .thenReturn(Mono.just(cliente));

        StepVerifier.create(validator.validate(cliente.getDocumentoIdentidad()))
                .expectNext(cliente)
                .verifyComplete();
    }
}
