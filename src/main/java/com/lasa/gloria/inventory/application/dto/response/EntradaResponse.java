package com.lasa.gloria.inventory.application.dto.response;

import java.time.Instant;
import java.util.List;

public record EntradaResponse(
        Integer id,
        String numero,
        Integer proveedorId,
        Instant fecha,
        String estado,
        Integer usuarioId,
        String observacion,
        List<EntradaDetalleResponse> detalles) {
}
