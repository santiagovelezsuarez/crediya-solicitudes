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
public class SolicitudEntity {
    @Id
    @Column("id_solicitud")
    private UUID id;
    private  UUID idUsuario;
    private short idEstado;
    private BigDecimal monto;
    private int plazoEnMeses;
    private UUID idTipoPrestamo;
}
