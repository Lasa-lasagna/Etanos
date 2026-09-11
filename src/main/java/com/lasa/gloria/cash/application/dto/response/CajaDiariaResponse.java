package com.lasa.gloria.cash.application.dto.response;

import com.lasa.gloria.cash.domain.model.CajaEstado;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record CajaDiariaResponse(
        Integer id,
        LocalDate fecha,
        Integer usuarioId,
        BigDecimal montoInicial,
        BigDecimal montoFinalSistema,
        BigDecimal montoFinalReal,
        BigDecimal diferencia,
        CajaEstado estado,
        Instant openedAt,
        Instant closedAt
) {}
