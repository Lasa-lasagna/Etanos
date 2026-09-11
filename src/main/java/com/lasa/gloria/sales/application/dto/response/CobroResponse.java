package com.lasa.gloria.sales.application.dto.response;

import com.lasa.gloria.sales.domain.model.MetodoPago;
import java.math.BigDecimal;
import java.time.Instant;

public record CobroResponse(
        Integer id,
        Integer ventaId,
        String ventaNumero,
        Instant fecha,
        BigDecimal monto,
        MetodoPago metodoPago,
        String referencia,
        String observacion
) {}
