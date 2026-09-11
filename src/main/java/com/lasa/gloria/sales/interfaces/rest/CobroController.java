package com.lasa.gloria.sales.interfaces.rest;

import com.lasa.gloria.sales.application.dto.request.RegistrarCobroRequest;
import com.lasa.gloria.sales.application.dto.response.CobroResponse;
import com.lasa.gloria.sales.application.service.CobroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
// import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cobros")
@RequiredArgsConstructor
public class CobroController {

    private final CobroService service;

    @PostMapping
    @ResponseStatus(org.springframework.http.HttpStatus.CREATED)
    public CobroResponse registrar(@Valid @RequestBody RegistrarCobroRequest req) {
        return service.registrarCobro(req);
    }
}