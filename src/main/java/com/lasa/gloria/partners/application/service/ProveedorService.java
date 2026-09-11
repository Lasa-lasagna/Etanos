package com.lasa.gloria.partners.application.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.partners.application.dto.request.CrearProveedorRequest;
import com.lasa.gloria.partners.application.dto.request.ActualizarProveedorRequest;
import com.lasa.gloria.partners.application.dto.response.ProveedorResponse;
import com.lasa.gloria.partners.application.mapper.ProveedorMapper;
import com.lasa.gloria.partners.domain.model.Cliente;
import com.lasa.gloria.partners.domain.model.Proveedor;
import com.lasa.gloria.partners.domain.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper mapper;

    @Transactional
    public ProveedorResponse crear(CrearProveedorRequest req) {
        if (proveedorRepository.existsByNroDoc(req.nroDoc())) {
            throw new com.lasa.gloria.common.exception.BusinessException(
                    "El proveedor con ese número de documento ya existe",
                    "PROVEEDOR_EXISTS",
                    org.springframework.http.HttpStatus.CONFLICT
            );
        }
        Proveedor proveedor = Proveedor.builder()
                .tipoDoc(req.tipoDoc())
                .nroDoc(req.nroDoc())
                .razonSocial(req.razonSocial())
                .nombreComercial(req.nombreComercial())
                .telefono(req.telefono())
                .direccion(req.direccion())
                .condicionPago(req.condicionPago() != null ? req.condicionPago() : 0)
                .estado(true)
                .build();
        return mapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Transactional(readOnly = true)
    public Page<ProveedorResponse> listar(Pageable pageable) {
        return proveedorRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProveedorResponse obtener(Integer id) {
        return mapper.toResponse(proveedorRepository.findById(id)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Proveedor", id)));
    }

    @Transactional
    public ProveedorResponse actualizar(Integer id, ActualizarProveedorRequest req) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Proveedor", id));

        if (req.telefono() != null) proveedor.setTelefono(req.telefono());
        if (req.direccion() != null) proveedor.setDireccion(req.direccion());
        if (req.condicionPago() != null) proveedor.setCondicionPago(req.condicionPago());
        if (req.estado() != null) proveedor.setEstado(req.estado());

        return mapper.toResponse(proveedorRepository.save(proveedor));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!proveedorRepository.existsById(id)) {
            throw new com.lasa.gloria.common.exception.NotFoundException("Proveedor", id);
        }

        Proveedor proveedor = proveedorRepository.findById(id)
            .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Proveedor", id));

            proveedor.setEstado(false);

        proveedorRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public Page<com.lasa.gloria.partners.application.dto.response.ProveedorResponse> buscar(String term, Pageable pageable) {
        return proveedorRepository.buscar(term, pageable).map(mapper::toResponse);
    }
}