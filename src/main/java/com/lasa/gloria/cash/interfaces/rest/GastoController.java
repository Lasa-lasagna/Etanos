package com.lasa.gloria.cash.interfaces.rest;

import com.lasa.gloria.cash.application.dto.request.ActualizarGastoRequest;
import com.lasa.gloria.cash.application.dto.request.CrearGastoRequest;
import com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse;
import com.lasa.gloria.cash.application.service.GastoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gastos")
@RequiredArgsConstructor
public class GastoController {

    private final GastoService service;

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public GastoOperativoResponse crear(@Valid @RequestBody CrearGastoRequest req) {
        return service.crear(req);
    }

    @PutMapping("/{id}")
    public GastoOperativoResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ActualizarGastoRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    @GetMapping
    public Page<GastoOperativoResponse> listar(
            @RequestParam(required = false) java.time.LocalDate inicio,
            @RequestParam(required = false) java.time.LocalDate fin,
            @RequestParam(required = false) String categoria,
            org.springframework.data.domain.Pageable pageable) {

        if (categoria != null && !categoria.isBlank()) {
            return service.listarPorCategoria(categoria, pageable);
        }
        if (inicio != null && fin != null) {
            return service.listarPorFecha(inicio, fin, pageable);
        }
        return service.listar(pageable);
    }
}