package com.lasa.gloria.partners.interfaces.rest;

import com.lasa.gloria.partners.application.dto.request.CrearProveedorRequest;
import com.lasa.gloria.partners.application.dto.request.ActualizarProveedorRequest;
import com.lasa.gloria.partners.application.dto.response.ProveedorResponse;
import com.lasa.gloria.partners.application.service.ProveedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProveedorResponse crear(@Valid @RequestBody CrearProveedorRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public Page<ProveedorResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/buscar")
    public Page<ProveedorResponse> buscar(@RequestParam String term, Pageable pageable) {
        return service.buscar(term, pageable);
    }

    @GetMapping("/{id}")
    public ProveedorResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    public ProveedorResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ActualizarProveedorRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}