package co.pragma.api.adapter;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseServiceTest {

    private final ResponseService responseService = new ResponseService();

    @Test
    void createdJsonShouldReturn201WithJsonBody() {
        String body = "test-body";

        Mono<ServerResponse> responseMono = responseService.createdJson(body);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(response.headers().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                })
                .verifyComplete();
    }

    @Test
    void okJsonShouldReturn200WithJsonBody() {
        String body = "test-body";

        Mono<ServerResponse> responseMono = responseService.okJson(body);

        StepVerifier.create(responseMono)
                .assertNext(response -> {
                    assertThat(response.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(response.headers().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
                })
                .verifyComplete();
    }
}
