package com.lasa.gloria.inventory.interfaces.rest;

import com.lasa.gloria.inventory.application.dto.request.CrearAjusteRequest;
import com.lasa.gloria.inventory.application.dto.response.AjusteResponse;
import com.lasa.gloria.inventory.application.service.AjusteService;
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
@RequestMapping("/api/ajustes")
@RequiredArgsConstructor
public class AjusteController {

    private final AjusteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AjusteResponse crear(@Valid @RequestBody CrearAjusteRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public Page<AjusteResponse> listar(
            Pageable pageable,
            @RequestParam(required = false) Instant desde,
            @RequestParam(required = false) Instant hasta) {
        return service.listar(pageable, desde, hasta);
    }

    @GetMapping("/{id}")
    public AjusteResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}/confirmar")
    public AjusteResponse confirmar(@PathVariable Integer id) {
        return service.confirmar(id);
    }

    @PutMapping("/{id}/anular")
    public AjusteResponse anular(@PathVariable Integer id) {
        return service.anular(id);
    }
}
