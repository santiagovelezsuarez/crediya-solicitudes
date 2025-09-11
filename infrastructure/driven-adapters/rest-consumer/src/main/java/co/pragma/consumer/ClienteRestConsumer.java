package co.pragma.consumer;

import co.pragma.error.ErrorCode;
import co.pragma.exception.InfrastructureException;
import co.pragma.model.cliente.*;
import co.pragma.model.cliente.gateways.UsuarioPort;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteRestConsumer implements UsuarioPort {

    private final WebClient webClient;
    private final TokenProvider tokenProvider;

    @Override
    public Flux<ClienteInfo> getClientesByIdIn(List<UUID> userIds) {
        return tokenProvider.getCurrentToken()
                .flatMapMany(token -> callAuthService(userIds, token));
    }

    @CircuitBreaker(name = "AUTH_SERVICE", fallbackMethod = "fallbackGetClientesByIdIn")
    private Flux<ClienteInfo> callAuthService(List<UUID> userIds, String token) {
        Mono<ClientesInfoList> response = webClient.post()
                .uri("/usuarios/batch")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(Map.of("userIds", userIds))
                .retrieve()
                .bodyToMono(ClientesInfoList.class)
                .onErrorMap(ex -> new InfrastructureException(ErrorCode.DB_ERROR.name(), ex));

        return response.flatMapMany(r -> Flux.fromIterable(r.data()));
    }

    private Flux<ClienteInfo> fallbackGetClientesByIdIn(List<UUID> userIds, Throwable throwable) {
        log.error("Circuit Breaker activado. Fallo en la llamada a ms-auth: {}", throwable.getMessage());
        return Flux.error(new InfrastructureException(ErrorCode.TECHNICAL_ERROR.name(), throwable));
    }
}
