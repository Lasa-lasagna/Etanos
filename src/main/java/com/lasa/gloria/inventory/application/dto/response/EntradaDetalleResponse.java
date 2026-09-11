package com.lasa.gloria.inventory.application.dto.response;

import java.math.BigDecimal;

public record EntradaDetalleResponse(
        Integer id,
        Integer productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioCompra,
        BigDecimal subtotal) {
}
