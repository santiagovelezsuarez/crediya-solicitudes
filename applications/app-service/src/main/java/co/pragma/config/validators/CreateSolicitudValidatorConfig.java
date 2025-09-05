package co.pragma.config.validators;

import co.pragma.model.cliente.gateways.UsuariosPort;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.businessrules.ClienteValidator;
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
    public ClienteValidator clienteValidator(UsuariosPort usuariosPort) {
        return new ClienteValidator(usuariosPort);
    }
}
