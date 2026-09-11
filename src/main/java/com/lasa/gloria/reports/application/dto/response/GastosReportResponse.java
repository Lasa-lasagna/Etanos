package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record GastosReportResponse(
        Map<String, BigDecimal> porCategoria,
        Map<String, BigDecimal> porMetodoPago
) {}