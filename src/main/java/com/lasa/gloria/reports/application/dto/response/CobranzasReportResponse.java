package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;
import java.util.Map;

public record CobranzasReportResponse(
        Map<String, BigDecimal> porMetodoPago
) {}