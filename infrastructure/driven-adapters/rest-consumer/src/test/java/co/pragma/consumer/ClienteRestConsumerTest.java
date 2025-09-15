package co.pragma.consumer;


import co.pragma.model.cliente.projection.ClienteInfo;
import co.pragma.model.cliente.projection.ClientesInfoList;
import co.pragma.model.session.gateways.TokenProvider;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestHeadersSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class ClienteRestConsumerTest {

    @Mock
    private WebClient webClient;

    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RequestBodySpec requestBodySpec;

    @Mock
    private RequestHeadersSpec requestHeadersSpec;

    @Mock
    private ResponseSpec responseSpec;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @InjectMocks
    private ClienteRestConsumer clienteRestConsumer;

    private List<UUID> userIds;

    @BeforeEach
    void setUp() {
        userIds = List.of(UUID.randomUUID());

        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(tokenProvider.getCurrentToken()).thenReturn(Mono.just("token"));
        when(requestBodyUriSpec.uri(any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(any(String.class), any(String.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.bodyValue(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void shouldReturnClientes_onSuccess() {
        ClienteInfo clienteInfo = new ClienteInfo(UUID.randomUUID(), "test@test.com", "Test User", BigDecimal.valueOf(1250000), new BigDecimal("100"));
        ClientesInfoList mockResponse = new ClientesInfoList(List.of(clienteInfo));

        when(responseSpec.bodyToMono(ClientesInfoList.class)).thenReturn(Mono.just(mockResponse));

        StepVerifier.create(clienteRestConsumer.getClientesByIdIn(userIds))
                .expectNextMatches(c -> c.id().equals(clienteInfo.id()))
                .verifyComplete();
    }
}