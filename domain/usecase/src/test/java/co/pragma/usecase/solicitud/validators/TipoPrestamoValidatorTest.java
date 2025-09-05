package co.pragma.usecase.solicitud.validators;

import co.pragma.exception.TipoPrestamoNotFoundException;
import co.pragma.model.tipoprestamo.TipoPrestamoVO;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoValidatorTest {

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @Test
    void shouldFailWhenTipoPrestamoDoesNotExist() {
        TipoPrestamoVO tipoPrestamo = TipoPrestamoVO.builder()
                .nombre("PERSONAL")
                .build();

        TipoPrestamoValidator validator = new TipoPrestamoValidator(tipoPrestamoRepository);

        when(tipoPrestamoRepository.findByNombre(tipoPrestamo.nombre()))
                .thenReturn(Mono.empty());

        StepVerifier.create(validator.validate(tipoPrestamo))
                .expectErrorMatches(TipoPrestamoNotFoundException.class::isInstance)
                .verify();
    }
}
