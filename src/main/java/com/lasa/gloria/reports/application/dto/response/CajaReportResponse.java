package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record CajaReportResponse(
        Map<String, BigDecimal> ingresosPorMetodo,
        Map<String, BigDecimal> egresosPorMetodo,
        BigDecimal montoInicial,
        BigDecimal diferencia
) {}