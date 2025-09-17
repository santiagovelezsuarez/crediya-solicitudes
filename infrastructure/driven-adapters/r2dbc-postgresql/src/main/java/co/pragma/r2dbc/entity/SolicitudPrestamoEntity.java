package co.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "solicitudes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SolicitudPrestamoEntity {
    @Id
    @Column("id_solicitud")
    private UUID id;
    private String codigo;
    private UUID idCliente;
    private Integer idEstado;
    private BigDecimal monto;
    private Integer plazoEnMeses;
    private BigDecimal tasaInteres;
    private UUID idTipoPrestamo;
    private Boolean notificado;
}
