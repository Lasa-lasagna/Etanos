package com.lasa.gloria.inventory.application.dto.response;

import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import com.lasa.gloria.inventory.domain.model.TipoAjuste;

import java.time.Instant;
import java.util.List;

public record AjusteResponse(
        Integer id,
        String numero,
        TipoAjuste tipo,
        Instant fecha,
        EstadoDocumento estado,
        String motivo,
        Integer usuarioId,
        List<AjusteDetalleResponse> detalles) {
}
