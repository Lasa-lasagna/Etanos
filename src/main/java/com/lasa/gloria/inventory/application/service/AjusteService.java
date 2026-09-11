package com.lasa.gloria.inventory.application.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.inventory.application.dto.request.CrearAjusteRequest;
import com.lasa.gloria.inventory.application.dto.response.AjusteResponse;
import com.lasa.gloria.inventory.application.mapper.AjusteMapper;
import com.lasa.gloria.inventory.domain.model.Ajuste;
import com.lasa.gloria.inventory.domain.model.AjusteDetalle;
import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import com.lasa.gloria.inventory.domain.model.Producto;
import com.lasa.gloria.inventory.domain.repository.AjusteRepository;
import com.lasa.gloria.inventory.domain.repository.ProductoRepository;
import com.lasa.gloria.inventory.domain.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AjusteService {

    private final AjusteRepository ajusteRepository;
    private final ProductoRepository productoRepository;
    private final StockService stockService;
    private final AjusteMapper mapper;

    @Transactional
    public AjusteResponse crear(CrearAjusteRequest req) {
        Ajuste ajuste = Ajuste.builder()
                .numero(generarNumero())
                .tipo(req.tipo())
                .motivo(req.motivo())
                .usuarioId(req.usuarioId())
                .fecha(Instant.now())
                .estado(EstadoDocumento.BORRADOR)
                .build();

        List<AjusteDetalle> detalles = req.detalles().stream().map(d -> {
            Producto producto = productoRepository.findById(d.productoId())
                    .orElseThrow(() -> new NotFoundException("Producto", d.productoId()));
            return AjusteDetalle.builder()
                    .ajuste(ajuste)
                    .producto(producto)
                    .cantidad(d.cantidad())
                    .build();
        }).toList();
        ajuste.getDetalles().addAll(detalles);

        return mapper.toResponse(ajusteRepository.save(ajuste));
    }

    @Transactional(readOnly = true)
    public Page<AjusteResponse> listar(Pageable pageable) {
        return ajusteRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<AjusteResponse> listar(Pageable pageable, Instant desde, Instant hasta) {
        if (desde != null && hasta != null) {
            return ajusteRepository.findByFechaBetween(desde, hasta, pageable).map(mapper::toResponse);
        }
        return ajusteRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public AjusteResponse obtener(Integer id) {
        return mapper.toResponse(ajusteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ajuste", id)));
    }

    @Transactional
    public AjusteResponse confirmar(Integer id) {
        Ajuste ajuste = ajusteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ajuste", id));
        if (ajuste.getEstado() != EstadoDocumento.BORRADOR) {
            throw new BusinessException("Ajuste no esta en estado BORRADOR", "AJUSTE_NO_CONFIRMABLE", HttpStatus.CONFLICT);
        }
        stockService.procesarAjuste(ajuste);
        ajuste.confirmar();
        return mapper.toResponse(ajusteRepository.save(ajuste));
    }

    @Transactional
    public AjusteResponse anular(Integer id) {
        Ajuste ajuste = ajusteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ajuste", id));
        if (ajuste.getEstado() != EstadoDocumento.CONFIRMADO) {
            throw new BusinessException("Solo se pueden anular ajustes CONFIRMADOS", "AJUSTE_NO_ANULABLE", HttpStatus.CONFLICT);
        }
        stockService.reversarAjuste(ajuste);
        ajuste.anular();
        return mapper.toResponse(ajusteRepository.save(ajuste));
    }

    private String generarNumero() {
        return "AJ-" + String.format("%06d", ajusteRepository.nextNumero());
    }
}
