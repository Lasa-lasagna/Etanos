package com.lasa.gloria.reports.interfaces.rest;

import com.lasa.gloria.reports.application.dto.request.ReportFilterRequest;
import com.lasa.gloria.reports.application.dto.response.ReportPeriodoResponse;
import com.lasa.gloria.reports.application.dto.response.VentasReportResponse;
import com.lasa.gloria.reports.application.dto.response.CobranzasReportResponse;
import com.lasa.gloria.reports.application.dto.response.CajaReportResponse;
import com.lasa.gloria.reports.application.dto.response.GastosReportResponse;
import com.lasa.gloria.reports.application.dto.response.CuentasCobrarReportResponse;
import com.lasa.gloria.reports.application.dto.response.StockCriticoReportResponse;
import com.lasa.gloria.reports.application.dto.response.EntradasReportResponse;
import com.lasa.gloria.reports.application.service.VentasReportService;
import com.lasa.gloria.reports.application.service.CobranzasReportService;
import com.lasa.gloria.reports.application.service.CajaReportService;
import com.lasa.gloria.reports.application.service.GastosReportService;
import com.lasa.gloria.reports.application.service.CuentasCobrarReportService;
import com.lasa.gloria.reports.application.service.StockReportService;
import com.lasa.gloria.reports.application.service.EntradasReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportsController {

    private final VentasReportService ventasService;
    private final CobranzasReportService cobranzasService;
    private final CajaReportService cajaService;
    private final GastosReportService gastosService;
    private final CuentasCobrarReportService cuentasCobrarService;
    private final StockReportService stockService;
    private final EntradasReportService entradasService;

    @GetMapping("/ventas")
    public ResponseEntity<ReportPeriodoResponse<VentasReportResponse>> ventas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String agrupacion,
            @RequestParam(required = false) Integer stockMinimo
    ) {
        ReportFilterRequest req = new ReportFilterRequest(desde, hasta, agrupacion, null);
        return ResponseEntity.ok(ventasService.generar(req));
    }

    @GetMapping("/cobranzas")
    public ResponseEntity<ReportPeriodoResponse<CobranzasReportResponse>> cobranzas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String agrupacion,
            @RequestParam(required = false) Integer stockMinimo
    ) {
        ReportFilterRequest req = new ReportFilterRequest(desde, hasta, agrupacion, null);
        return ResponseEntity.ok(cobranzasService.generar(req));
    }

    @GetMapping("/caja")
    public ResponseEntity<ReportPeriodoResponse<CajaReportResponse>> caja(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String agrupacion,
            @RequestParam(required = false) Integer stockMinimo
    ) {
        ReportFilterRequest req = new ReportFilterRequest(desde, hasta, agrupacion, null);
        return ResponseEntity.ok(cajaService.generar(req));
    }

    @GetMapping("/gastos")
    public ResponseEntity<ReportPeriodoResponse<GastosReportResponse>> gastos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String agrupacion,
            @RequestParam(required = false) Integer stockMinimo
    ) {
        ReportFilterRequest req = new ReportFilterRequest(desde, hasta, agrupacion, null);
        return ResponseEntity.ok(gastosService.generar(req));
    }

    @GetMapping("/cuentas-cobrar/vencidas")
    public ResponseEntity<ReportPeriodoResponse<CuentasCobrarReportResponse>> cuentasCobrarVencidas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false, defaultValue = "tramo") String agrupacion
    ) {
        ReportFilterRequest req = new ReportFilterRequest(null, hasta, agrupacion, null);
        return ResponseEntity.ok(cuentasCobrarService.generar(req));
    }

    @GetMapping("/stock/critico")
    public ResponseEntity<ReportPeriodoResponse<StockCriticoReportResponse>> stockCritico(
            @RequestParam(defaultValue = "8") Integer stockMinimo
    ) {
        ReportFilterRequest req = new ReportFilterRequest(null, null, null, stockMinimo);
        return ResponseEntity.ok(stockService.generar(req));
    }

    @GetMapping("/entradas")
    public ResponseEntity<ReportPeriodoResponse<EntradasReportResponse>> entradas(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String agrupacion,
            @RequestParam(required = false) Integer stockMinimo
    ) {
        ReportFilterRequest req = new ReportFilterRequest(desde, hasta, agrupacion, null);
        return ResponseEntity.ok(entradasService.generar(req));
    }
}