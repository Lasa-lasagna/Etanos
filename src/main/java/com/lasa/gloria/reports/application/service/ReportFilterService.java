package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class ReportFilterService {

    public record ReportFilter(
            java.time.LocalDate desde,
            java.time.LocalDate hasta,
            Agrupacion agrupacion,
            Integer stockMinimo
    ) {}

    public enum Agrupacion {
        DIA, SEMANA, MES
    }

    public ReportFilter normalize(ReportFilterRequest req) {
        LocalDate hasta = req.hasta() != null ? req.hasta() : java.time.LocalDate.now();
        LocalDate desde = req.desde() != null ? req.desde() : hasta;

        if (desde.isAfter(hasta)) {
            throw new BusinessException("Fecha 'desde' no puede ser posterior a 'hasta'", "FILTRO_INVALIDO", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        Agrupacion agrupacion = parseAgrupacion(req.agrupacion(), desde, hasta);
        Integer stockMinimo = req.stockMinimo() != null ? req.stockMinimo() : 8;

        return new ReportFilter(desde, hasta, agrupacion, stockMinimo);
    }

    private Agrupacion parseAgrupacion(String input, LocalDate desde, LocalDate hasta) {
        if (input != null) {
            String normalized = input.trim().toUpperCase();
            if ("DIA".equals(normalized)) return Agrupacion.DIA;
            if ("SEMANA".equals(normalized)) return Agrupacion.SEMANA;
            if ("MES".equals(normalized)) return Agrupacion.MES;
        }
        // Auto-detección
        long dias = java.time.temporal.ChronoUnit.DAYS.between(desde, hasta) + 1;
        if (dias > 31) return Agrupacion.MES;
        if (dias > 7) return Agrupacion.SEMANA;
        return Agrupacion.DIA;
    }

    public String formatoFecha(Agrupacion agrupacion) {
        return switch (agrupacion) {
            case DIA -> "yyyy-MM-dd";
            case SEMANA -> "yyyy-'W'ww";
            case MES -> "yyyy-MM";
        };
    }

    public String truncateFechaSql(Agrupacion agrupacion) {
        return switch (agrupacion) {
            case DIA -> "DATE_TRUNC('day', fecha)";
            case SEMANA -> "DATE_TRUNC('week', fecha)";
            case MES -> "DATE_TRUNC('month', fecha)";
        };
    }

    public String formatPeriodo(java.time.LocalDate fecha, Agrupacion agrupacion) {
        return switch (agrupacion) {
            case DIA -> fecha.toString();
            case SEMANA -> fecha.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-'W'ww"));
            case MES -> fecha.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));
        };
    }
}