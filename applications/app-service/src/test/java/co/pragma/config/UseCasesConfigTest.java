package co.pragma.config;

import co.pragma.model.cliente.gateways.UsuarioPort;
import co.pragma.model.estadosolicitud.gateways.EstadoSolicitudRepository;
import co.pragma.model.solicitudprestamo.gateways.SolicitudPrestamoRepository;
import co.pragma.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.pragma.usecase.solicitud.AprobarSolicitudPrestamoUseCase;
import co.pragma.usecase.solicitud.ListarSolicitudesRevisionManualUseCase;
import co.pragma.usecase.solicitud.SolicitarPrestamoUseCase;
import co.pragma.usecase.solicitud.businessrules.TipoPrestamoValidator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {

        @Bean
        public UsuarioPort usuariosPort() {
            return Mockito.mock(UsuarioPort.class);
        }

        @Bean
        public EstadoSolicitudRepository estadoSolicitudRepository() {
            return Mockito.mock(EstadoSolicitudRepository.class);
        }

        @Bean
        public SolicitudPrestamoRepository solicitudPrestamoRepository() {
            return Mockito.mock(SolicitudPrestamoRepository.class);
        }

        @Bean
        public TipoPrestamoRepository tipoPrestamoRepository() {
            return Mockito.mock(TipoPrestamoRepository.class);
        }

        @Bean
        public SolicitarPrestamoUseCase solicitudPrestamoUseCase() {
            return Mockito.mock(SolicitarPrestamoUseCase.class);
        }

        @Bean
        public ListarSolicitudesRevisionManualUseCase listarSolicitudesRevisionManualUseCase() {
            return Mockito.mock(ListarSolicitudesRevisionManualUseCase.class);
        }

        @Bean
        public AprobarSolicitudPrestamoUseCase aprobarSolicitudPrestamoUseCase() {
            return Mockito.mock(AprobarSolicitudPrestamoUseCase.class);
        }

        @Bean
        public TipoPrestamoValidator tipoPrestamoValidator() {
            return Mockito.mock(TipoPrestamoValidator.class);
        }
    }

}