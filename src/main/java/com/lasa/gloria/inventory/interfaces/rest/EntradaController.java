package com.lasa.gloria.inventory.interfaces.rest;

import com.lasa.gloria.inventory.application.dto.request.CrearEntradaRequest;
import com.lasa.gloria.inventory.application.dto.response.EntradaResponse;
import com.lasa.gloria.inventory.application.service.EntradaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/api/entradas")
@RequiredArgsConstructor
public class EntradaController {

    private final EntradaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntradaResponse crear(@Valid @RequestBody CrearEntradaRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public Page<EntradaResponse> listar(
            Pageable pageable,
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta) {
        return service.listar(pageable, desde, hasta);
    }

    @GetMapping("/{id}")
    public EntradaResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}/confirmar")
    public EntradaResponse confirmar(@PathVariable Integer id) {
        return service.confirmar(id);
    }

    @PutMapping("/{id}/anular")
    public EntradaResponse anular(@PathVariable Integer id) {
        return service.anular(id);
    }
}
