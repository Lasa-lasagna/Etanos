package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;

public record TramoVencidoItem(
        String tramo,
        Integer cantidad,
        BigDecimal monto
) {}