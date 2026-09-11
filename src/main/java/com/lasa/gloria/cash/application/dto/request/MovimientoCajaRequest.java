package com.lasa.gloria.cash.application.dto.request;

import com.lasa.gloria.cash.domain.model.TipoMovimientoCaja;
import com.lasa.gloria.cash.domain.model.OrigenMovimiento;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record MovimientoCajaRequest(
        @NotNull TipoMovimientoCaja tipo,
        @NotNull OrigenMovimiento origen,
        Integer refId,
        @NotNull MetodoPago metodoPago,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal monto,
        String descripcion
) {}