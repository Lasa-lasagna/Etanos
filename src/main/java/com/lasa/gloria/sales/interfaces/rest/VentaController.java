package com.lasa.gloria.sales.interfaces.rest;

import com.lasa.gloria.sales.application.dto.request.CrearVentaRequest;
import com.lasa.gloria.sales.application.dto.response.VentaResponse;
import com.lasa.gloria.sales.application.dto.response.VentasPendientesResponse;
import com.lasa.gloria.sales.application.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final VentaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VentaResponse crear(@Valid @RequestBody CrearVentaRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public Page<VentaResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    public VentaResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}/confirmar")
    public VentaResponse confirmar(@PathVariable Integer id,
            @RequestParam(required = false) Boolean entregaBombona) {
        return service.confirmar(id, entregaBombona);
    }


    @PutMapping("/{id}/anular")
    public VentaResponse anular(@PathVariable Integer id) {
        return service.anular(id);
    }

    @GetMapping("/credito-pendiente")
    public Page<VentasPendientesResponse> creditoPendiente(Pageable pageable) {
        return service.listarCreditoPendiente(pageable);
    }
}