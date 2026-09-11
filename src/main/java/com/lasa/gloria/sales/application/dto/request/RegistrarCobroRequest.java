package com.lasa.gloria.sales.application.dto.request;

import com.lasa.gloria.sales.domain.model.MetodoPago;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record   RegistrarCobroRequest(
        @NotNull Integer ventaId,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal monto,
        @NotNull MetodoPago metodoPago,
        String referencia,
        String observacion,
        Integer usuarioId
) {}