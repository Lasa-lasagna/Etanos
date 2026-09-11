package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record StockCriticoReportResponse(
        Integer stockMinimo,
        List<StockCriticoItem> items
) {}