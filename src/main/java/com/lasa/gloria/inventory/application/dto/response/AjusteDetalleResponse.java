package com.lasa.gloria.inventory.application.dto.response;

public record AjusteDetalleResponse(
        Integer id,
        Integer productoId,
        String productoNombre,
        Integer cantidad) {
}
