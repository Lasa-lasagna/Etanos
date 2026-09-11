package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.StockCriticoReportResponse;
import com.lasa.gloria.reports.application.dto.response.StockCriticoItem;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReportFilterService filterService;

    @Transactional(readOnly = true)
    public com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<StockCriticoReportResponse> generar(ReportFilterRequest req) {
        var filter = filterService.normalize(req);
        Integer umbral = filter.stockMinimo();

        String sql = """
            SELECT 
                p.id,
                p.nombre,
                i.stock_actual,
                i.stock_actual * p.costo_promedio as valor_stock
            FROM inventario i
            JOIN productos p ON p.id = i.producto_id
            WHERE i.stock_actual <= :umbral
            ORDER BY i.stock_actual ASC
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("umbral", umbral);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        List<StockCriticoItem> items = new ArrayList<>();
        for (Object[] row : rows) {
            items.add(new StockCriticoItem(
                    ((Number) row[0]).intValue(),
                    row[1].toString(),
                    ((Number) row[2]).intValue(),
                    umbral,
                    (BigDecimal) row[3]
            ));
        }

        StockCriticoReportResponse extra = new StockCriticoReportResponse(umbral, items);

        return new com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<>(
                new PeriodoInfo(LocalDate.now(), LocalDate.now(), "dia", "yyyy-MM-dd"),
                new ResumenResponse(BigDecimal.ZERO, items.size(), BigDecimal.ZERO),
                List.of(),
                new DesgloseResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()),
                extra
        );
    }
}