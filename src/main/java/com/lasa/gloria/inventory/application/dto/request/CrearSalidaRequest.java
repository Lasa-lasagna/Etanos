package com.lasa.gloria.inventory.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CrearSalidaRequest(
        Integer clienteId,
        @NotNull Integer usuarioId,
        String observacion,
        @NotEmpty @Valid List<SalidaDetalleRequest> detalles) {
}
