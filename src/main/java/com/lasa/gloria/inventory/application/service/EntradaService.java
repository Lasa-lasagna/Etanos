package com.lasa.gloria.inventory.application.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.inventory.application.dto.request.CrearEntradaRequest;
// import com.lasa.gloria.inventory.application.dto.request.EntradaDetalleRequest;
// import com.lasa.gloria.inventory.application.dto.response.EntradaDetalleResponse;
import com.lasa.gloria.inventory.application.dto.response.EntradaResponse;
import com.lasa.gloria.inventory.application.mapper.EntradaMapper;
import com.lasa.gloria.inventory.domain.model.Entrada;
import com.lasa.gloria.inventory.domain.model.EntradaDetalle;
import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import com.lasa.gloria.inventory.domain.model.Producto;
import com.lasa.gloria.inventory.domain.repository.EntradaRepository;
import com.lasa.gloria.inventory.domain.repository.ProductoRepository;
import com.lasa.gloria.inventory.domain.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EntradaService {

    private final EntradaRepository entradaRepository;
    private final ProductoRepository productoRepository;
    private final StockService stockService;
    private final EntradaMapper mapper;

    @Transactional
    public EntradaResponse crear(CrearEntradaRequest req) {
        Entrada entrada = Entrada.builder()
                .numero(generarNumero())
                .proveedorId(req.proveedorId())
                .usuarioId(req.usuarioId())
                .observacion(req.observacion())
                .fecha(Instant.now())
                .estado(EstadoDocumento.BORRADOR)
                .build();

        List<EntradaDetalle> detalles = req.detalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new NotFoundException("Producto", d.productoId()));
            BigDecimal subtotal = d.precioCompra()
                    .multiply(BigDecimal.valueOf(d.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            return EntradaDetalle.builder()
                    .entrada(entrada)
                    .producto(producto)
                    .cantidad(d.cantidad())
                    .precioCompra(d.precioCompra())
                    .subtotal(subtotal)
                    .build();
        }).toList();
        entrada.getDetalles().addAll(detalles);

        return mapper.toResponse(entradaRepository.save(entrada));
    }

    @Transactional(readOnly = true)
    public Page<EntradaResponse> listar(Pageable pageable) {
        return entradaRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<EntradaResponse> listar(Pageable pageable, Instant desde, Instant hasta) {
        if (desde != null && hasta != null) {
            return entradaRepository.findByFechaBetween(desde, hasta, pageable).map(mapper::toResponse);
        }
        return entradaRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public EntradaResponse obtener(Integer id) {
        return mapper.toResponse(entradaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entrada", id)));
    }

    @Transactional
    public EntradaResponse confirmar(Integer id) {
        Entrada entrada = entradaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entrada", id));
        if (entrada.getEstado() != EstadoDocumento.BORRADOR) {
            throw new BusinessException("Entrada no esta en estado BORRADOR", "ENTRADA_NO_CONFIRMABLE", HttpStatus.CONFLICT);
        }
        stockService.procesarEntrada(entrada);
        entrada.confirmar();
        return mapper.toResponse(entradaRepository.save(entrada));
    }

    @Transactional
    public EntradaResponse anular(Integer id) {
        Entrada entrada = entradaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Entrada", id));
        if (entrada.getEstado() != EstadoDocumento.CONFIRMADO) {
            throw new BusinessException("Solo se pueden anular entradas CONFIRMADAS", "ENTRADA_NO_ANULABLE", HttpStatus.CONFLICT);
        }
        stockService.reversarEntrada(entrada);
        entrada.anular();
        return mapper.toResponse(entradaRepository.save(entrada));
    }

    private String generarNumero() {
        return "E-" + String.format("%06d", entradaRepository.nextNumero());
    }
}
