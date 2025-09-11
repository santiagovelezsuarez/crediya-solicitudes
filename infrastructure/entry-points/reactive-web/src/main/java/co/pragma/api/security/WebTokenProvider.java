package co.pragma.api.security;

import co.pragma.exception.business.ForbiddenException;
import co.pragma.model.cliente.TokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebTokenProvider implements TokenProvider {

    @Override
    public Mono<String> getCurrentToken() {
        return Mono.deferContextual(contextView -> {
            try {
                String token = contextView.get("JWT_TOKEN");
                log.debug("Token obtenido del contexto global");
                return Mono.just(token);
            } catch (Exception e) {
                log.error("Token no encontrado en el contexto", e);
                return Mono.error(new ForbiddenException());
            }
        });
    }
}
