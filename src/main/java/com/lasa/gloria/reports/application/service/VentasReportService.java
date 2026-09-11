package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.VentasReportResponse;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentasReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReportFilterService filterService;

    @Transactional(readOnly = true)
    public com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<VentasReportResponse> generar(
            ReportFilterRequest req) {
        var filter = filterService.normalize(req);
        String truncateSql = filterService.truncateFechaSql(filter.agrupacion());

        LocalDate hasta = req.hasta() != null ? req.hasta() : LocalDate.now();
        LocalDate desde = req.desde() != null ? req.desde() : hasta;

        String sql = """
                SELECT
                    %s as periodo,
                    tipo_venta,
                    metodo_pago,
                    COUNT(*) as cantidad,
                    SUM(total) as total
                FROM salidas
                WHERE fecha BETWEEN :desde AND :hasta
                  AND estado = 'CONFIRMADO'
                GROUP BY 1, tipo_venta, metodo_pago
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
                        Collectors.toList()));

        List<SeriesItem> series = new ArrayList<>();
        Map<String, BigDecimal> porTipoVenta = new LinkedHashMap<>();
        Map<String, BigDecimal> porMetodoPago = new LinkedHashMap<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;
        Integer cantidadGeneral = 0;

        for (Map.Entry<String, List<Object[]>> entry : grouped.entrySet()) {
            String periodo = entry.getKey();
            List<Object[]> rowsPeriodo = entry.getValue();

            BigDecimal totalPeriodo = BigDecimal.ZERO;
            Integer cantidadPeriodo = 0;
            Map<String, BigDecimal> desglosePeriodo = new LinkedHashMap<>();

            for (Object[] row : rowsPeriodo) {
                String tipoVenta = row[1].toString();
                String metodoPago = row[2].toString();
                Long cantidad = ((Number) row[3]).longValue();
                BigDecimal total = (BigDecimal) row[4];

                totalPeriodo = totalPeriodo.add(total);
                cantidadPeriodo += cantidad.intValue();
                desglosePeriodo.merge(tipoVenta + "_" + metodoPago, total, BigDecimal::add);
                porTipoVenta.merge(tipoVenta, total, BigDecimal::add);
                porMetodoPago.merge(metodoPago, total, BigDecimal::add);
            }

            series.add(new SeriesItem(periodo, totalPeriodo, desglosePeriodo, cantidadPeriodo));
            totalGeneral = totalGeneral.add(totalPeriodo);
            cantidadGeneral += cantidadPeriodo;
        }

        BigDecimal ticketPromedio = BigDecimal.ZERO;
        if (cantidadGeneral > 0) {
            ticketPromedio = totalGeneral.divide(BigDecimal.valueOf(cantidadGeneral), 2,
                    java.math.RoundingMode.HALF_UP);
        }

        var resumen = new ResumenResponse(totalGeneral, cantidadGeneral, ticketPromedio);
        VentasReportResponse extra = new VentasReportResponse(
                Map.of("CONTADO", BigDecimal.ZERO, "CREDITO", BigDecimal.ZERO),
                Map.of("EFECTIVO", BigDecimal.ZERO, "YAPE", BigDecimal.ZERO, "PLIN", BigDecimal.ZERO, "TRANSFERENCIA",
                        BigDecimal.ZERO, "MIXTO", BigDecimal.ZERO));

        return new ReportPeriodoResponse<>(
                new PeriodoInfo(LocalDate.now(), LocalDate.now(), "dia", "yyyy-MM-dd"),
                // new ResumenResponse(BigDecimal.ZERO, 0, BigDecimal.ZERO),
                new ResumenResponse(BigDecimal.ZERO, 0, BigDecimal.ZERO),

                List.of(),
                new DesgloseResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()),
                extra);
    }
}