package co.pragma.config.validators;

import co.pragma.api.security.SecurityContextSessionProvider;
import co.pragma.model.cliente.PermissionValidator;
import co.pragma.model.cliente.gateways.SessionProvider;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;

import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateSolicitudValidatorConfig {

    @Bean
    public TipoPrestamoValidator tipoPrestamoValidator(TipoPrestamoRepository repository) {
        return new TipoPrestamoValidator(repository);
    }

    @Bean
    public SessionProvider sessionProvider() {
        return new SecurityContextSessionProvider();
    }

    @Bean
    public PermissionValidator permissionValidator(SessionProvider sessionProvider) {
        return new PermissionValidator(sessionProvider);
    }
}
