package com.lasa.gloria.inventory.application.service;

import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.inventory.application.dto.request.CrearMarcaRequest;
import com.lasa.gloria.inventory.application.dto.response.MarcaResponse;
import com.lasa.gloria.inventory.application.mapper.MarcaMapper;
import com.lasa.gloria.inventory.domain.model.Marca;
import com.lasa.gloria.inventory.domain.repository.MarcaRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final MarcaMapper mapper;

    @Transactional
    public MarcaResponse crear(CrearMarcaRequest req) {
        Marca marca = Marca.builder()
                .nombre(req.nombre())
                .descripcion(req.descripcion())
                .estado(true)
                .build();
        return mapper.toResponse(marcaRepository.save(marca));
    }

    @Transactional(readOnly = true)
    public MarcaResponse obtener(Integer id) {
        return mapper.toResponse(marcaRepository.findById(id).orElseThrow(() -> new NotFoundException("Marca", id)));
    }

    @Transactional
    public MarcaResponse actualizar(Integer id, CrearMarcaRequest req) {
        Marca marca = marcaRepository.findById(id).orElseThrow(() -> new NotFoundException("Marca", id));
        marca.setNombre(req.nombre());
        marca.setDescripcion(req.descripcion());
        return mapper.toResponse(marca);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!marcaRepository.existsById(id)) {
            throw new NotFoundException("Marca", id);
        }
        marcaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<MarcaResponse> listarOption() {
        return marcaRepository.findByEstadoTrue()
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<MarcaResponse> listar(Pageable pageable){
        return marcaRepository.findByEstadoTrue(pageable).map(mapper::toResponse);
    }
}
