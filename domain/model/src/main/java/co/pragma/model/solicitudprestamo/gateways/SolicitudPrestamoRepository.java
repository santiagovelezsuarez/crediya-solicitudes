package co.pragma.model.solicitudprestamo.gateways;

import co.pragma.model.solicitudprestamo.SolicitudPrestamo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface SolicitudPrestamoRepository {
    Mono<SolicitudPrestamo> save(SolicitudPrestamo solicitudPrestamo);
    Mono<SolicitudPrestamo> findByCodigo(String codigo);
    Flux<SolicitudPrestamo> findByIdEstadoIn(List<Integer> estados, int page, int size);
    Mono<Void> markAsNotificado(String codigo, Boolean notificado);
}
