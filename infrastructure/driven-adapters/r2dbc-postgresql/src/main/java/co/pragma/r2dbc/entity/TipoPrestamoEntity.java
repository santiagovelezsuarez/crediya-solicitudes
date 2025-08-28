package co.pragma.r2dbc.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Table(name = "tipo_prestamos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class TipoPrestamoEntity {
    @Id
    @Column("id_tipo_prestamo" )
    private UUID id;
    private String nombre;
    private BigDecimal montoMinimo;
    private BigDecimal montoMaximo;
    private BigDecimal tasaInteres;
    private boolean validacionAutomatica;
}
