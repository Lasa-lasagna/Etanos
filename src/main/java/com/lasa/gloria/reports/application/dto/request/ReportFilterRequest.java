package com.lasa.gloria.reports.application.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record ReportFilterRequest(
        LocalDate desde,
        LocalDate hasta,
        String agrupacion,
        @Min(0) Integer stockMinimo
) {}