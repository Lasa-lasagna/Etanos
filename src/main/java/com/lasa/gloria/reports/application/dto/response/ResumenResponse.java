package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;

public record ResumenResponse(
        BigDecimal total,
        Integer cantidad,
        BigDecimal ticketPromedio
) {}