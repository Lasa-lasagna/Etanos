package com.lasa.gloria.cash.application.dto.response;

import com.lasa.gloria.sales.domain.model.MetodoPago;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CajaResumenDiarioResponse(
        LocalDate fecha,
        BigDecimal montoInicial,
        BigDecimal totalIngresos,
        BigDecimal totalEgresos,
        BigDecimal montoFinalSistema,
        BigDecimal montoFinalReal,
        BigDecimal diferencia,
        java.util.Map<String, BigDecimal> ingresosPorMetodo,
        java.util.Map<String, BigDecimal> egresosPorMetodo
) {}