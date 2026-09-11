package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record SeriesItem(
        String periodo,
        BigDecimal total,
        Map<String, BigDecimal> desglose,
        Integer cantidad
) {}