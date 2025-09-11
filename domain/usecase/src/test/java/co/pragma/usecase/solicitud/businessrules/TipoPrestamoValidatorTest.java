package co.pragma.usecase.solicitud.businessrules;

import co.pragma.exception.business.MontoPrestamoOutOfRangeException;
import co.pragma.exception.business.TipoPrestamoNotFoundException;
import co.pragma.model.solicitudprestamo.SolicitarPrestamoCommand;
import co.pragma.model.tipoprestamo.TipoPrestamo;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TipoPrestamoValidatorTest {

    @Mock
    private TipoPrestamoRepository tipoPrestamoRepository;

    @InjectMocks
    private TipoPrestamoValidator tipoPrestamoValidator;

    private TipoPrestamo tipoPrestamo;

    @BeforeEach
    void setUp() {
        tipoPrestamo = TipoPrestamo.builder()
                .nombre("VIVIENDA")
                .montoMinimo(new BigDecimal("1000000"))
                .montoMaximo(new BigDecimal("50000000"))
                .build();
    }

    @Test
    void validateMontoWithinRange() {
        mockTipoPrestamo("VIVIENDA", tipoPrestamo);
        verifyValidationCompletes(new BigDecimal("25000000"));
    }

    @Test
    void throwExceptionWhenTipoPrestamoNotFound() {
        mockTipoPrestamo("COMERCIAL", null);
        verifyValidationThrows("COMERCIAL", new BigDecimal("500000"), TipoPrestamoNotFoundException.class);
    }

    @Test
    void throwExceptionWhenMontoBelowMin() {
        mockTipoPrestamo("VIVIENDA", tipoPrestamo);
        verifyValidationThrows("VIVIENDA", new BigDecimal("500000"), MontoPrestamoOutOfRangeException.class);
    }

    @Test
    void throwExceptionWhenMontoAboveMax() {
        mockTipoPrestamo("VIVIENDA", tipoPrestamo);
        verifyValidationThrows("VIVIENDA", new BigDecimal("60000000"), MontoPrestamoOutOfRangeException.class);
    }

    @Test
    void validateMontoAtMinBoundary() {
        mockTipoPrestamo("VIVIENDA", tipoPrestamo);
        verifyValidationCompletes(tipoPrestamo.getMontoMinimo());
    }

    @Test
    void validateMontoAtMaxBoundary() {
        mockTipoPrestamo("VIVIENDA", tipoPrestamo);
        verifyValidationCompletes(tipoPrestamo.getMontoMaximo());
    }

    private void mockTipoPrestamo(String nombre, TipoPrestamo tipoPrestamo) {
        when(tipoPrestamoRepository.findByNombre(nombre))
                .thenReturn(tipoPrestamo != null ? Mono.just(tipoPrestamo) : Mono.empty());
    }

    private void verifyValidationCompletes(BigDecimal monto) {
        StepVerifier.create(tipoPrestamoValidator.validate(SolicitarPrestamoCommand.builder().tipoPrestamo("VIVIENDA").monto(monto).build()))
                .verifyComplete();
    }

    private void verifyValidationThrows(String nombre, BigDecimal monto, Class<? extends Throwable> exception) {
        StepVerifier.create(tipoPrestamoValidator.validate(SolicitarPrestamoCommand.builder().tipoPrestamo(nombre).monto(monto).build()))
                .expectError(exception)
                .verify();
    }
}
