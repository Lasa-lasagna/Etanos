package com.lasa.gloria.cash.application.dto.response;

import com.lasa.gloria.sales.domain.model.MetodoPago;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record GastoOperativoResponse(
        Integer id,
        LocalDate fecha,
        String categoria,
        String descripcion,
        BigDecimal monto,
        MetodoPago metodoPago,
        Integer proveedorId,
        String comprobanteUrl,
        Integer usuarioId,
        Instant createdAt
) {}
