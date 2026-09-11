package com.lasa.gloria.cash.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CierreCajaRequest(
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal montoFinalReal,
        String observacion
) {}