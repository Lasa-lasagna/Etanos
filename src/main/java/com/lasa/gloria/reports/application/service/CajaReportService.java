package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.CajaReportResponse;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CajaReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReportFilterService filterService;

    @Transactional(readOnly = true)
    public com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<CajaReportResponse> generar(ReportFilterRequest req) {
        var filter = filterService.normalize(req);
        String truncateSql = filterService.truncateFechaSql(filter.agrupacion());

        LocalDate hasta = req.hasta() != null ? req.hasta() : LocalDate.now();
        LocalDate desde = req.desde() != null ? req.desde() : hasta;

        String sql = """
            SELECT 
                %s as periodo,
                tipo,
                origen,
                metodo_pago,
                COUNT(*) as cantidad,
                SUM(monto) as total
            FROM movimientos_caja m
            JOIN cajas_diarias c ON c.id = m.caja_diaria_id
            WHERE c.fecha BETWEEN :desde AND :hasta
            GROUP BY 1, tipo, origen, metodo_pago
            ORDER BY 1
            """.formatted(filterService.truncateFechaSql(filter.agrupacion()));

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("desde", desde.atStartOfDay());
        query.setParameter("hasta", java.time.LocalDateTime.of(hasta, java.time.LocalTime.MAX));

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        Map<String, List<Object[]>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> row[0].toString(),
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<SeriesItem> series = new ArrayList<>();
        Map<String, BigDecimal> ingresosPorMetodo = new LinkedHashMap<>();
        Map<String, BigDecimal> egresosPorMetodo = new LinkedHashMap<>();
        BigDecimal totalGeneral = BigDecimal.ZERO;

        Integer cantidadGeneral = 0;

        for (Map.Entry<String, List<Object[]>> entry : grouped.entrySet()) {
            String periodo = entry.getKey();
            BigDecimal totalPeriodo = BigDecimal.ZERO;
            Integer cantidadPeriodo = 0;

            for (Object[] row : entry.getValue()) {
                String tipo = row[1].toString();
                String origen = row[2].toString();
                String metodoPago = row[3].toString();
                Long cantidad = ((Number) row[4]).longValue();
                BigDecimal total = (BigDecimal) row[5];

                if ("INGRESO".equals(tipo)) {
                    ingresosPorMetodo.merge(metodoPago, total, BigDecimal::add);
                } else {
                    egresosPorMetodo.merge(metodoPago, total, BigDecimal::add);
                }
            }
        }

        CajaReportResponse extra = new CajaReportResponse(
                Map.of(), Map.of(), BigDecimal.ZERO, BigDecimal.ZERO
        );

        return new com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<>(
                new PeriodoInfo(LocalDate.now(), LocalDate.now(), "dia", "yyyy-MM-dd"),
                new ResumenResponse(BigDecimal.ZERO, 0, BigDecimal.ZERO),
                List.of(),
                new DesgloseResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()),
                extra
        );
    }
}