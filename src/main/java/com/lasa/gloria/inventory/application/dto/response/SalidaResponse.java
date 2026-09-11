package com.lasa.gloria.inventory.application.dto.response;

import java.time.Instant;
import java.util.List;

public record SalidaResponse(
        Integer id,
        String numero,
        Integer clienteId,
        Instant fecha,
        String estado,
        Integer usuarioId,
        String observacion,
        List<SalidaDetalleResponse> detalles) {
}
