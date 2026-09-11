package com.lasa.gloria.inventory.application.dto.response;

import java.math.BigDecimal;

public record SalidaDetalleResponse(
        Integer id,
        Integer productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioVenta,
        BigDecimal subtotal) {
}
