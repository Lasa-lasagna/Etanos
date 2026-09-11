package com.lasa.gloria.reports.application.dto.response;

import java.util.List;

public record ReportPeriodoResponse<T>(
        PeriodoInfo periodo,
        ResumenResponse resumen,
        List<SeriesItem> series,
        DesgloseResponse desglose,
        T extra
) {}