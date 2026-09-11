package com.lasa.gloria.inventory.application.service;

import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.inventory.application.dto.request.ActualizarProductoRequest;
import com.lasa.gloria.inventory.application.dto.request.CrearProductoRequest;
import com.lasa.gloria.inventory.application.dto.response.ProductoOptionResponse;
import com.lasa.gloria.inventory.application.dto.response.ProductoResponse;
import com.lasa.gloria.inventory.application.mapper.ProductoMapper;
import com.lasa.gloria.inventory.domain.model.Inventario;
import com.lasa.gloria.inventory.domain.model.Marca;
import com.lasa.gloria.inventory.domain.model.Producto;
import com.lasa.gloria.inventory.domain.repository.MarcaRepository;
import com.lasa.gloria.inventory.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MarcaRepository marcaRepository;
    private final ProductoMapper mapper;

    @Transactional
    public ProductoResponse crear(CrearProductoRequest req) {
        Marca marca = marcaRepository.findById(req.marcaId())
                .orElseThrow(() -> new NotFoundException("Marca", req.marcaId()));
        Producto producto = Producto.builder()
                .marca(marca)
                .nombre(req.nombre())
                .descripcion(req.descripcion())
                .precioCompra(req.precioCompra() != null ? req.precioCompra() : BigDecimal.ZERO)
                .precioVenta(req.precioVenta() != null ? req.precioVenta() : BigDecimal.ZERO)
                .estado(true)
                .build();
        producto.setInventario(Inventario.builder().producto(producto).stockActual(0).build());
        return mapper.toResponse(productoRepository.save(producto));
    }

    @Transactional(readOnly = true)
    public Page<ProductoResponse> listar(Pageable pageable) {
        return productoRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Integer id) {
        return mapper.toResponse(productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto", id)));
    }

    @Transactional
    public ProductoResponse actualizar(Integer id, ActualizarProductoRequest req) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Producto", id));
        if (req.nombre() != null) producto.setNombre(req.nombre());
        if (req.descripcion() != null) producto.setDescripcion(req.descripcion());
        if (req.precioCompra() != null) producto.setPrecioCompra(req.precioCompra());
        if (req.precioVenta() != null) producto.setPrecioVenta(req.precioVenta());
        if (req.estado() != null) producto.setEstado(req.estado());
        return mapper.toResponse(producto);
    }

    @Transactional
    public void eliminar(Integer id) {
        if (!productoRepository.existsById(id)) {
            throw new NotFoundException("Producto", id);
        }
        productoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ProductoOptionResponse> listarOpciones() {
        return productoRepository.findAllByEstadoTrueFetchStock().stream()
                .map(p -> new ProductoOptionResponse(
                        p.getId(),
                        p.getNombre(),
                        p.getPrecioVenta(),
                        p.getInventario() != null ? p.getInventario().getStockActual() : 0))
                .toList();
    }
}
