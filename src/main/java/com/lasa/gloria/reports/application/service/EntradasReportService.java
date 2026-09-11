package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.EntradasReportResponse;
import com.lasa.gloria.reports.application.dto.response.SeriesItem;
import com.lasa.gloria.reports.application.dto.response.DesgloseResponse;
import com.lasa.gloria.reports.application.dto.response.ResumenResponse;
import com.lasa.gloria.reports.application.dto.response.PeriodoInfo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntradasReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReportFilterService filterService;

    @Transactional(readOnly = true)
    public com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<EntradasReportResponse> generar(ReportFilterRequest req) {
        var filter = filterService.normalize(req);
        String truncateSql = filterService.truncateFechaSql(filter.agrupacion());

        LocalDate hasta = req.hasta() != null ? req.hasta() : LocalDate.now();
        LocalDate desde = req.desde() != null ? req.desde() : hasta;

        String sql = """
            SELECT 
                %s as periodo,
                COUNT(*) as cantidad,
                SUM(total) as total
            FROM entradas
            WHERE fecha BETWEEN :desde AND :hasta
              AND estado = 'CONFIRMADO'
            GROUP BY 1
            ORDER BY 1
            """.formatted(filterService.truncateFechaSql(filter.agrupacion()));

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("desde", desde.atStartOfDay(ZoneOffset.UTC).toInstant());
        query.setParameter("hasta", hasta.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<SeriesItem> series = new ArrayList<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;
        Integer cantidadGeneral = 0;

        for (Object[] row : rows) {
            String periodo = row[0].toString();
            Long cantidad = ((Number) row[1]).longValue();
            BigDecimal total = (BigDecimal) row[2];

            series.add(new SeriesItem(periodo, total, Map.of(), cantidad.intValue()));
            totalGeneral = totalGeneral.add(total);
            cantidadGeneral += cantidad.intValue();
        }

        BigDecimal ticketPromedio = BigDecimal.ZERO;
        if (cantidadGeneral > 0) {
            ticketPromedio = totalGeneral.divide(BigDecimal.valueOf(cantidadGeneral), 2, java.math.RoundingMode.HALF_UP);
        }

        var resumen = new ResumenResponse(totalGeneral, cantidadGeneral, ticketPromedio);
        EntradasReportResponse extra = new EntradasReportResponse();

        return new com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<>(
                new PeriodoInfo(LocalDate.now(), LocalDate.now(), "dia", "yyyy-MM-dd"),
                new ResumenResponse(BigDecimal.ZERO, 0, BigDecimal.ZERO),
                List.of(),
                new DesgloseResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()),
                extra
        );
    }
}