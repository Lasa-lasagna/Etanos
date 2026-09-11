package com.lasa.gloria.inventory.application.dto.response;

import java.math.BigDecimal;

public record ProductoResponse(
        Integer id,
        Integer marcaId,
        String marcaNombre,
        String nombre,
        String descripcion,
        BigDecimal precioCompra,
        BigDecimal precioVenta,
        BigDecimal costoPromedio,
        // Boolean estado,
        Integer stockActual

) {
}
