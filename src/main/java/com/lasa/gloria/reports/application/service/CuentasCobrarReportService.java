package com.lasa.gloria.reports.application.service;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.CuentasCobrarReportResponse;
import com.lasa.gloria.reports.application.dto.response.TramoVencidoItem;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentasCobrarReportService {

    @PersistenceContext
    private EntityManager entityManager;

    private final ReportFilterService filterService;

    @Transactional(readOnly = true)
    public com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<CuentasCobrarReportResponse> generar(ReportFilterRequest req) {
        var filter = filterService.normalize(req);

        LocalDate hasta = req.hasta() != null ? req.hasta() : LocalDate.now();
        LocalDate desde = req.desde() != null ? req.desde() : hasta;

        String sql = """
            SELECT 
                c.id as cliente_id,
                c.nombre as cliente_nombre,
                c.nro_doc,
                c.limite_credito,
                s.total,
                s.fecha_vencimiento,
                s.estado_factura
            FROM clientes c
            LEFT JOIN salidas s ON s.cliente_id = c.id AND s.tipo_venta = 'CREDITO'
            WHERE s.fecha_vencimiento <= :hasta
              AND s.estado_factura IN ('PENDIENTE','EMITIDA')
            ORDER BY s.fecha_vencimiento
            """;

        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("hasta", java.time.LocalDateTime.of(hasta, java.time.LocalTime.MAX));

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        Map<String, List<Object[]>> grouped = rows.stream()
                .collect(Collectors.groupingBy(
                        row -> {
                            LocalDate venc = (LocalDate) row[5];
                            long diff = java.time.temporal.ChronoUnit.DAYS.between(venc, LocalDate.now());
                            if (diff <= 30) return "0-30";
                            else if (diff <= 60) return "31-60";
                            else if (diff <= 90) return "61-90";
                            else return "90+";
                        },
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<TramoVencidoItem> tramos = new ArrayList<>();
        Map<String, BigDecimal> porTramo = new LinkedHashMap<>();
        BigDecimal totalVencido = BigDecimal.ZERO;

        for (Map.Entry<String, List<Object[]>> entry : grouped.entrySet()) {
            String tramo = entry.getKey();
            List<Object[]> rowsTramo = entry.getValue();

            BigDecimal montoTramo = BigDecimal.ZERO;
            Integer cantidadTramo = 0;

            for (Object[] row : rowsTramo) {
                BigDecimal total = (BigDecimal) row[4];
                montoTramo = montoTramo.add(total);
                cantidadTramo++;
            }

            tramos.add(new TramoVencidoItem(tramo, cantidadTramo, montoTramo));
            porTramo.put(tramo, montoTramo);
            totalVencido = totalVencido.add(montoTramo);
        }

        CuentasCobrarReportResponse extra = new CuentasCobrarReportResponse(tramos, totalVencido);

        return new com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse<>(
                new PeriodoInfo(LocalDate.now(), LocalDate.now(), "tramo", "tramo"),
                new ResumenResponse(totalVencido, 0, BigDecimal.ZERO),
                List.of(),
                new DesgloseResponse(Map.of(), Map.of(), Map.of(), Map.of(), Map.of()),
                extra
        );
    }
}