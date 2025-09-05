package co.pragma.r2dbc;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import co.pragma.r2dbc.adapter.SolicitudPrestamoReactiveRepositoryAdapter;
import co.pragma.r2dbc.entity.SolicitudPrestamoEntity;
import co.pragma.r2dbc.mapper.SolicitudPrestamoMapper;
import co.pragma.r2dbc.repository.SolicitudReactiveRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudPrestamoRepositoryAdapterTest {

    @InjectMocks
    SolicitudPrestamoReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    SolicitudReactiveRepository repository;

    @Mock
    SolicitudPrestamoMapper mapper;

    @Test
    void shouldSaveSolicitudPrestamo() {
        SolicitudPrestamo solicitudPrestamo = SolicitudPrestamo.builder()
                .idCliente(java.util.UUID.randomUUID())
                .idTipoPrestamo(java.util.UUID.randomUUID())
                .monto(new java.math.BigDecimal("5000000"))
                .plazoEnMeses(12)
                .build();

        SolicitudPrestamoEntity entity = SolicitudPrestamoEntity.builder()
                .idCliente(solicitudPrestamo.getIdCliente())
                .idTipoPrestamo(solicitudPrestamo.getIdTipoPrestamo())
                .monto(solicitudPrestamo.getMonto())
                .plazoEnMeses(solicitudPrestamo.getPlazoEnMeses())
                .build();

        when(repository.save(any())).thenReturn(Mono.just(entity));
        when(mapper.toEntity(solicitudPrestamo)).thenReturn(entity);
        when(mapper.toModel(entity)).thenReturn(solicitudPrestamo);

        StepVerifier.create(repositoryAdapter.save(solicitudPrestamo))
                .expectNext(solicitudPrestamo)
                .verifyComplete();

        verify(repository).save(any());
    }
}
