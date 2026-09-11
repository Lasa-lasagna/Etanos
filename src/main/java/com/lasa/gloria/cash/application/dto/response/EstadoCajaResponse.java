package com.lasa.gloria.cash.application.dto.response;

public record EstadoCajaResponse(
        boolean abierta,
        CajaDiariaResponse caja,
        CajaResumenDiarioResponse resumen
) {}
