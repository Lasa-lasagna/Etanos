package com.lasa.gloria.partners.application.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.partners.application.dto.request.CrearClienteRequest;
import com.lasa.gloria.partners.application.dto.request.ActualizarClienteRequest;
import com.lasa.gloria.partners.application.dto.response.ClienteOptionResponse;
import com.lasa.gloria.partners.application.dto.response.ClienteResponse;
import com.lasa.gloria.partners.application.mapper.ClienteMapper;
import com.lasa.gloria.partners.domain.model.Cliente;
import com.lasa.gloria.partners.domain.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper mapper;

    @Transactional
    public ClienteResponse crear(CrearClienteRequest req) {
        if (clienteRepository.existsByNroDoc(req.nroDoc())) {
            throw new BusinessException("El cliente con ese número de documento ya existe", "CLIENTE_EXISTS", org.springframework.http.HttpStatus.CONFLICT);
        }
        Cliente cliente = Cliente.builder()
                .tipoDoc(req.tipoDoc())
                .nroDoc(req.nroDoc())
                .nombre(req.nombre())
                .telefono(req.telefono())
                .direccion(req.direccion())
                .limiteCredito(req.limiteCredito() != null ? req.limiteCredito() : java.math.BigDecimal.ZERO)
                .diasCredito(req.diasCredito() != null ? req.diasCredito() : 0)
                .estado(true)
                .build();
        return mapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional(readOnly = true)
    public Page<ClienteResponse> listar(Pageable pageable) {
        return clienteRepository.findByEstadoTrue(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public com.lasa.gloria.partners.application.dto.response.ClienteResponse obtener(Integer id) {
        return mapper.toResponse(clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente", id)));
    }

    @Transactional
    public ClienteResponse actualizar(Integer id, com.lasa.gloria.partners.application.dto.request.ActualizarClienteRequest req) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Cliente", id));

        if (req.telefono() != null) cliente.setTelefono(req.telefono());
        if (req.direccion() != null) cliente.setDireccion(req.direccion());
        if (req.limiteCredito() != null) cliente.setLimiteCredito(req.limiteCredito());
        if (req.diasCredito() != null) cliente.setDiasCredito(req.diasCredito());
        if (req.estado() != null) cliente.setEstado(req.estado());

        return mapper.toResponse(clienteRepository.save(cliente));
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new NotFoundException("Cliente", id);
        }

        Cliente cliente = clienteRepository.findById(id)
            .orElseThrow(()-> new NotFoundException("Cliente", id));

        cliente.setEstado(false);

        clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<ClienteResponse> buscar(String term, org.springframework.data.domain.Pageable pageable) {
        return clienteRepository.buscar(term, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<ClienteOptionResponse> listarOpciones() {
        return clienteRepository.findByEstadoTrue().stream()
                .map(c -> new ClienteOptionResponse(
                        c.getId(),
                        c.getTipoDoc(),
                        c.getNroDoc(),
                        c.getNombre(),
                        c.getTelefono(),
                        c.getDireccion(),
                        c.getLimiteCredito(),
                        c.getEstado()))
                .toList();
    }
}