package com.lasa.gloria.cash.application.dto.response;

import com.lasa.gloria.cash.domain.model.TipoMovimientoCaja;
import com.lasa.gloria.cash.domain.model.OrigenMovimiento;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import java.math.BigDecimal;
import java.time.Instant;

public record MovimientoCajaResponse(
        Integer id,
        Integer cajaDiariaId,
        TipoMovimientoCaja tipo,
        OrigenMovimiento origen,
        Integer refId,
        MetodoPago metodoPago,
        BigDecimal monto,
        String descripcion,
        Instant fecha
) {}
