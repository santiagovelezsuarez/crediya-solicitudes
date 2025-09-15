package co.pragma.model.cliente.projection;

import java.util.List;

public record ClientesInfoList(
        List<ClienteInfo> data
) {}