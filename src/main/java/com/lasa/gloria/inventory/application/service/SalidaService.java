package com.lasa.gloria.inventory.application.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.inventory.application.dto.request.CrearSalidaRequest;
import com.lasa.gloria.inventory.application.dto.response.SalidaResponse;
import com.lasa.gloria.inventory.application.mapper.SalidaMapper;
import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import com.lasa.gloria.inventory.domain.model.Producto;
import com.lasa.gloria.inventory.domain.model.Salida;
import com.lasa.gloria.inventory.domain.model.SalidaDetalle;
import com.lasa.gloria.inventory.domain.repository.ProductoRepository;
import com.lasa.gloria.inventory.domain.repository.SalidaRepository;
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
public class SalidaService {

    private final SalidaRepository salidaRepository;
    private final ProductoRepository productoRepository;
    private final StockService stockService;
    private final SalidaMapper mapper;

    @Transactional
    public SalidaResponse crear(CrearSalidaRequest req) {
        Salida salida = Salida.builder()
                .numero(generarNumero())
                .clienteId(req.clienteId())
                .usuarioId(req.usuarioId())
                .observacion(req.observacion())
                .fecha(Instant.now())
                .estado(EstadoDocumento.BORRADOR)
                .build();

        List<SalidaDetalle> detalles = req.detalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new NotFoundException("Producto", d.productoId()));
            BigDecimal subtotal = d.precioVenta()
                    .multiply(BigDecimal.valueOf(d.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            return SalidaDetalle.builder()
                    .salida(salida)
                    .producto(producto)
                    .cantidad(d.cantidad())
                    .precioVenta(d.precioVenta())
                    .subtotal(subtotal)
                    .build();
        }).toList();
        salida.getDetalles().addAll(detalles);

        return mapper.toResponse(salidaRepository.save(salida));
    }

    @Transactional(readOnly = true)
    public Page<SalidaResponse> listar(Pageable pageable) {
        return salidaRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<SalidaResponse> listar(Pageable pageable, Instant desde, Instant hasta) {
        if (desde != null && hasta != null) {
            return salidaRepository.findByFechaBetween(desde, hasta, pageable).map(mapper::toResponse);
        }
        return salidaRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SalidaResponse obtener(Integer id) {
        return mapper.toResponse(salidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salida", id)));
    }

    @Transactional
    public SalidaResponse confirmar(Integer id) {
        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salida", id));
        if (salida.getEstado() != EstadoDocumento.BORRADOR) {
            throw new BusinessException("Salida no esta en estado BORRADOR", "SALIDA_NO_CONFIRMABLE", HttpStatus.CONFLICT);
        }
        stockService.procesarSalida(salida);
        salida.confirmar();
        return mapper.toResponse(salidaRepository.save(salida));
    }

    @Transactional
    public SalidaResponse anular(Integer id) {
        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Salida", id));
        if (salida.getEstado() != EstadoDocumento.CONFIRMADO) {
            throw new BusinessException("Solo se pueden anular salidas CONFIRMADAS", "SALIDA_NO_ANULABLE", HttpStatus.CONFLICT);
        }
        stockService.reversarSalida(salida);
        salida.anular();
        return mapper.toResponse(salidaRepository.save(salida));
    }

    private String generarNumero() {
        return "S-" + String.format("%06d", salidaRepository.nextNumero());
    }
}
