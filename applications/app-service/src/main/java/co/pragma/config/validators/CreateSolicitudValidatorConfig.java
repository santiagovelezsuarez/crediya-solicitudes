package co.pragma.config.validators;

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
}
