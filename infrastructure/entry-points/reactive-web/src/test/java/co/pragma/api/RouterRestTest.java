package co.pragma.api;

import co.pragma.api.handler.SolicitudPrestamoHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ContextConfiguration(classes = {RouterRest.class, SolicitudPrestamoHandler.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

//    @Test
//    void testListenGETUseCase() {
//        webTestClient.get()
//                .uri("/api/usecase/path")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .value(userResponse -> {
//                            Assertions.assertThat(userResponse).isEmpty();
//                        }
//                );
//    }
//
//    @Test
//    void testListenGETOtherUseCase() {
//        webTestClient.get()
//                .uri("/api/otherusercase/path")
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .value(userResponse -> {
//                            Assertions.assertThat(userResponse).isEmpty();
//                        }
//                );
//    }
//
//    @Test
//    void testListenPOSTUseCase() {
//        webTestClient.post()
//                .uri("/api/usecase/otherpath")
//                .accept(MediaType.APPLICATION_JSON)
//                .bodyValue("")
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody(String.class)
//                .value(userResponse -> {
//                            Assertions.assertThat(userResponse).isEmpty();
//                        }
//                );
//    }
}
