package com.lasa.gloria.inventory.interfaces.rest;

import com.lasa.gloria.inventory.application.dto.request.CrearMarcaRequest;
import com.lasa.gloria.inventory.application.dto.response.MarcaResponse;
import com.lasa.gloria.inventory.application.service.MarcaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarcaResponse crear(@Valid @RequestBody CrearMarcaRequest req) {
        return service.crear(req);
    }

    @GetMapping("/{id}")
    public MarcaResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    public MarcaResponse actualizar(@PathVariable Integer id, @Valid @RequestBody CrearMarcaRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }

    @GetMapping
    public Page<MarcaResponse> listar(Pageable pageable){
        return service.listar(pageable);
    }

    @GetMapping("/options")
    public List<MarcaResponse> listarOptions() {
        return service.listarOption();
    }
    
}
