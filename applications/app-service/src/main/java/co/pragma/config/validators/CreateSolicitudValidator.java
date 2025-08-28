package co.pragma.config.validators;

import co.pragma.base.gateways.BusinessValidator;
import co.pragma.model.solicitud.Solicitud;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.businessrules.SolicitudValidationPolicy;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoExistsValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.List;

@Configuration
public class CreateSolicitudValidator {

    @Bean
    public TipoPrestamoExistsValidator tipoPrestamoExistsValidator(TipoPrestamoRepository tipoPrestamoRepository) {
        return new TipoPrestamoExistsValidator(tipoPrestamoRepository);
    }

    @Bean
    @Primary
    public BusinessValidator<Solicitud> registrationValidator(TipoPrestamoExistsValidator tipoPrestamoExistsValidator) {
        return new SolicitudValidationPolicy(List.of(
                tipoPrestamoExistsValidator
        ));
    }

}
