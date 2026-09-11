package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.CobranzasReportResponse;
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
public class CobranzasReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReportFilterService filterService;

    @Transactional(readOnly = true)
    public com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<CobranzasReportResponse> generar(ReportFilterRequest req) {
        var filter = filterService.normalize(req);
        String truncateSql = filterService.truncateFechaSql(filter.agrupacion());

        LocalDate hasta = req.hasta() != null ? req.hasta() : LocalDate.now();
        LocalDate desde = req.desde() != null ? req.desde() : hasta;

        String sql = """
            SELECT 
                %s as periodo,
                metodo_pago,
                COUNT(*) as cantidad,
                SUM(monto) as total
            FROM cobros
            WHERE fecha BETWEEN :desde AND :hasta
            GROUP BY 1, metodo_pago
            ORDER BY 1
            """.formatted(filterService.truncateFechaSql(filter.agrupacion()));

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("desde", desde.atStartOfDay(ZoneOffset.UTC).toInstant());
        query.setParameter("hasta", hasta.atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        Map<String, List<Object[]>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row[0].toString(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<SeriesItem> series = new ArrayList<>();
        Map<String, BigDecimal> porMetodoPago = new LinkedHashMap<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;
        Integer cantidadGeneral = 0;

        for (Map.Entry<String, List<Object[]>> entry : grouped.entrySet()) {
            String periodo = entry.getKey();
            List<Object[]> rowsPeriodo = entry.getValue();

            BigDecimal totalPeriodo = BigDecimal.ZERO;
            Integer cantidadPeriodo = 0;
            Map<String, BigDecimal> desglosePeriodo = new LinkedHashMap<>();

            for (Object[] row : entry.getValue()) {
                String metodoPago = row[1].toString();
                Long cantidad = ((Number) row[2]).longValue();
                BigDecimal total = (BigDecimal) row[3];

                totalPeriodo = totalPeriodo.add(total);
                cantidadPeriodo += cantidad.intValue();
                porMetodoPago.merge(metodoPago, total, BigDecimal::add);
            }

            series.add(new SeriesItem(periodo, totalPeriodo, Map.of(), cantidadPeriodo));
            totalGeneral = totalGeneral.add(totalPeriodo);
        }

        BigDecimal ticketPromedio = BigDecimal.ZERO;
        if (cantidadGeneral > 0) {
            ticketPromedio = totalGeneral.divide(BigDecimal.valueOf(cantidadGeneral), 2, java.math.RoundingMode.HALF_UP);
        }

        var resumen = new ResumenResponse(totalGeneral, cantidadGeneral, ticketPromedio);
        CobranzasReportResponse extra = new CobranzasReportResponse(Map.of());

        return new com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<>(
                new PeriodoInfo(LocalDate.now(), LocalDate.now(), "dia", "yyyy-MM-dd"),
                new ResumenResponse(BigDecimal.ZERO, 0, BigDecimal.ZERO),
                List.of(),
                new DesgloseResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()),
                extra
        );
    }
}