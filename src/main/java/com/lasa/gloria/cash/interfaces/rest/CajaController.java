package com.lasa.gloria.cash.interfaces.rest;

import com.lasa.gloria.cash.application.dto.request.AperturaCajaRequest;
import com.lasa.gloria.cash.application.dto.request.CierreCajaRequest;
import com.lasa.gloria.cash.application.dto.request.MovimientoCajaRequest;
import com.lasa.gloria.cash.application.dto.response.CajaDiariaResponse;
import com.lasa.gloria.cash.application.dto.response.CajaResumenDiarioResponse;
import com.lasa.gloria.cash.application.dto.response.EstadoCajaResponse;
import com.lasa.gloria.cash.application.dto.response.MovimientoCajaResponse;
import com.lasa.gloria.cash.application.service.CajaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
// import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

// import java.time.LocalDate;

@RestController
@RequestMapping("/api/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService service;

    @PostMapping("/apertura")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public CajaDiariaResponse apertura(@Valid @RequestBody AperturaCajaRequest req) {
        return service.apertura(req);
    }

    @GetMapping("/estado")
    public EstadoCajaResponse estado(
            @RequestParam(required = false) java.time.LocalDate fecha) {
        return service.estado(fecha);
    }

    @GetMapping("/{id}/movimientos")
    public List<MovimientoCajaResponse> movimientos(@PathVariable Integer id) {
        return service.movimientosDeCaja(id);
    }

    @PostMapping("/{id}/movimiento")
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public MovimientoCajaResponse movimiento(
            @PathVariable Integer id, @Valid @RequestBody MovimientoCajaRequest req) {
        return service.movimiento(id, req);
    }

    @PutMapping("/{id}/cierre")
    public CajaDiariaResponse cierre(
            @PathVariable Integer id, @Valid @RequestBody CierreCajaRequest req) {
        return service.cierre(id, req);
    }

    @GetMapping("/resumen")
    public CajaResumenDiarioResponse resumenDiario(
            @RequestParam(required = false) java.time.LocalDate fecha) {
        return service.resumenDiario(fecha != null ? fecha : java.time.LocalDate.now());
    }
}