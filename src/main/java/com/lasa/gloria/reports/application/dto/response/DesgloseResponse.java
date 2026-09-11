package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record DesgloseResponse(
        Map<String, BigDecimal> porTipoVenta,
        Map<String, BigDecimal> porMetodoPago,
        Map<String, BigDecimal> porCategoria,
        Map<String, BigDecimal> ingresosPorMetodo,
        Map<String, BigDecimal> egresosPorMetodo
) {}