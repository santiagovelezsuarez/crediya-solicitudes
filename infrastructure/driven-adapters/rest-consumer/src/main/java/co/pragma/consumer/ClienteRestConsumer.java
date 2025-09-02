package co.pragma.consumer;

import co.pragma.exception.ClienteNotFoundException;
import co.pragma.model.cliente.Cliente;
import co.pragma.model.cliente.DocumentoIdentidadVO;
import co.pragma.model.cliente.gateways.UsuariosPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteRestConsumer implements UsuariosPort {

    private final WebClient webClient;

    @Override
    public Mono<Cliente> findClienteByDocumento(DocumentoIdentidadVO documentoIdentidad) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/usuario/{tipo}/{numero}")
                        .build(documentoIdentidad.tipoDocumento(), documentoIdentidad.numeroDocumento()))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals, response ->
                        Mono.error(new ClienteNotFoundException(
                                "Cliente no encontrado: " + documentoIdentidad.tipoDocumento() + " " + documentoIdentidad.numeroDocumento()))
                )
                .onStatus(HttpStatus.INTERNAL_SERVER_ERROR::equals, response ->
                        Mono.error(new RuntimeException("Error en servicio de usuarios"))
                )
                .bodyToMono(Cliente.class)
                .doOnNext(cliente -> log.info("Cliente encontrado: {}", cliente.getId()));
    }

    @CircuitBreaker(name = "testPost")
    public Mono<ObjectResponse> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return webClient
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }

}
