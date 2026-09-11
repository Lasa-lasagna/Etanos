package com.lasa.gloria.inventory.application.dto.request;

import com.lasa.gloria.inventory.domain.model.TipoAjuste;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CrearAjusteRequest(
        @NotNull TipoAjuste tipo,
        @NotNull Integer usuarioId,
        @Size(max = 255) String motivo,
        @NotEmpty @Valid List<AjusteDetalleRequest> detalles) {
}
