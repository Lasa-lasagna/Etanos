package com.lasa.gloria.partners.interfaces.rest;

import com.lasa.gloria.partners.application.dto.request.CrearClienteRequest;
import com.lasa.gloria.partners.application.dto.request.ActualizarClienteRequest;
import com.lasa.gloria.partners.application.dto.response.ClienteOptionResponse;
import com.lasa.gloria.partners.application.dto.response.ClienteResponse;
import com.lasa.gloria.partners.application.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody CrearClienteRequest req) {
        return service.crear(req);
    }

    @GetMapping
    public Page<ClienteResponse> listar(Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/opciones")
    public List<ClienteOptionResponse> opciones() {
        return service.listarOpciones();
    }

    @GetMapping("/buscar")
    public Page<ClienteResponse> buscar(@RequestParam String term, Pageable pageable) {
        return service.buscar(term, pageable);
    }

    @GetMapping("/{id}")
    public ClienteResponse obtener(@PathVariable Integer id) {
        return service.obtener(id);
    }

    @PutMapping("/{id}")
    public ClienteResponse actualizar(@PathVariable Integer id, @Valid @RequestBody ActualizarClienteRequest req) {
        return service.actualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        service.eliminar(id);
    }
}