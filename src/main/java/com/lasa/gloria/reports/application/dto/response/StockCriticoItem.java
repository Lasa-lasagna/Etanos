package com.lasa.gloria.reports.application.dto.response;

import java.math.BigDecimal;

public record StockCriticoItem(
        Integer productoId,
        String productoNombre,
        Integer stockActual,
        Integer umbral,
        BigDecimal valorStock
) {}