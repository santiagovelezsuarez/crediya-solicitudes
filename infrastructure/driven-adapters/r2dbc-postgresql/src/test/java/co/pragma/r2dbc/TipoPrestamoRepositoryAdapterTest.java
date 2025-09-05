package co.pragma.r2dbc;

import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.r2dbc.adapter.TipoPrestamoReactiveRepositoryAdapter;
import co.pragma.r2dbc.entity.TipoPrestamoEntity;
import co.pragma.r2dbc.repository.TipoPrestamoReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoRepositoryAdapterTest {

    @InjectMocks
    TipoPrestamoReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    TipoPrestamoReactiveRepository reactiveRepository;

    @Mock
    ObjectMapper mapper;

    @Test
    void shouldFindByNombre() {
        String nombre = "Personal";

        TipoPrestamoEntity entity = TipoPrestamoEntity.builder()
                .id(UUID.randomUUID())
                .nombre("Personal")
                .montoMinimo(BigDecimal.valueOf(100000))
                .montoMaximo(BigDecimal.valueOf(10000000))
                .tasaInteres(BigDecimal.valueOf(0.05))
                .validacionAutomatica(true)
                .build();

        TipoPrestamo model = TipoPrestamo.builder()
                .id(entity.getId())
                .nombre(entity.getNombre())
                .montoMinimo(entity.getMontoMinimo())
                .montoMaximo(entity.getMontoMaximo())
                .tasaInteres(entity.getTasaInteres())
                .validacionAutomatica(entity.isValidacionAutomatica())
                .build();

        when(reactiveRepository.findByNombre(any())).thenReturn(Mono.just(entity));
        when(mapper.map(entity, TipoPrestamo.class)).thenReturn(model);

        StepVerifier.create(repositoryAdapter.findByNombre(nombre))
                .expectNextMatches(tp -> tp.getNombre().equals("Personal"))
                .verifyComplete();

        verify(reactiveRepository).findByNombre(any());
        verify(mapper).map(entity, TipoPrestamo.class);
    }


}
