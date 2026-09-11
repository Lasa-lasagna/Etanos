package com.lasa.gloria.cash.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record AperturaCajaRequest(
        @NotNull Integer usuarioId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal montoInicial,
        String observacion
) {}

