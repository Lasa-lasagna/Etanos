package com.lasa.gloria.reports.application.dto.response;

import java.time.LocalDate;

public record PeriodoInfo(
        LocalDate desde,
        LocalDate hasta,
        String agrupacion,
        String formatoFecha
) {}