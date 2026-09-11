package com.lasa.gloria.cash.application.service;

import com.lasa.gloria.cash.application.dto.request.ActualizarGastoRequest;
import com.lasa.gloria.cash.application.dto.request.CrearGastoRequest;
import com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse;
import com.lasa.gloria.cash.application.dto.response.MovimientoCajaResponse;
import com.lasa.gloria.cash.domain.model.EstadoGasto;
import com.lasa.gloria.cash.domain.model.GastoOperativo;
import com.lasa.gloria.cash.domain.model.CajaDiaria;
import com.lasa.gloria.cash.domain.model.CajaEstado;
import com.lasa.gloria.cash.domain.model.MovimientoCaja;
import com.lasa.gloria.cash.domain.model.TipoMovimientoCaja;
import com.lasa.gloria.cash.domain.model.OrigenMovimiento;
import com.lasa.gloria.cash.domain.repository.CajaDiariaRepository;
import com.lasa.gloria.cash.domain.repository.GastoOperativoRepository;
import com.lasa.gloria.cash.domain.repository.MovimientoCajaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GastoService {

    private final GastoOperativoRepository gastoRepository;
    private final CajaDiariaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoRepository;

    @Transactional
    public com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse crear(com.lasa.gloria.cash.application.dto.request.CrearGastoRequest req) {
        CajaDiaria caja = cajaRepository.findByEstadoAndFecha(CajaEstado.ABIERTA, LocalDate.now())
                .orElseThrow(() -> new com.lasa.gloria.common.exception.BusinessException(
                        "No hay caja abierta para hoy", "CAJA_NO_ABIERTA", org.springframework.http.HttpStatus.CONFLICT
                ));

        GastoOperativo gasto = GastoOperativo.builder()
                .fecha(req.fecha())
                .categoria(req.categoria())
                .descripcion(req.descripcion())
                .monto(req.monto())
                .metodoPago(req.metodoPago())
                .proveedorId(req.proveedorId())
                .comprobanteUrl(req.comprobanteUrl())
                .usuarioId(req.usuarioId())
                .build();

        GastoOperativo saved = gastoRepository.save(gasto);

        // Crear movimiento de caja EGRESO
        MovimientoCaja mov = MovimientoCaja.builder()
                .cajaDiariaId(caja.getId())
                .tipo(com.lasa.gloria.cash.domain.model.TipoMovimientoCaja.EGRESO)
                .origen(com.lasa.gloria.cash.domain.model.OrigenMovimiento.GASTO)
                .refId(saved.getId())
                .metodoPago(req.metodoPago())
                .monto(req.monto())
                .descripcion(req.descripcion())
                .build();

        movimientoRepository.save(mov);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse> listar(org.springframework.data.domain.Pageable pageable) {
        return gastoRepository.findByEstado(EstadoGasto.ACTIVO, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse> listarPorFecha(LocalDate inicio, LocalDate fin, org.springframework.data.domain.Pageable pageable) {
        return gastoRepository.findByEstadoAndFechaBetween(EstadoGasto.ACTIVO, inicio, fin, pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse> listarPorCategoria(String categoria, org.springframework.data.domain.Pageable pageable) {
        return gastoRepository.findByEstadoAndCategoria(EstadoGasto.ACTIVO, categoria, pageable).map(this::toResponse);
    }

    @Transactional
    public GastoOperativoResponse actualizar(Integer id, ActualizarGastoRequest req) {
        GastoOperativo gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.BusinessException(
                        "Gasto no encontrado", "GASTO_NO_ENCONTRADO", org.springframework.http.HttpStatus.NOT_FOUND
                ));

        if (req.getFecha() != null) gasto.setFecha(req.getFecha());
        if (req.getCategoria() != null) gasto.setCategoria(req.getCategoria());
        if (req.getDescripcion() != null) gasto.setDescripcion(req.getDescripcion());
        if (req.getMonto() != null) gasto.setMonto(req.getMonto());
        if (req.getMetodoPago() != null) gasto.setMetodoPago(req.getMetodoPago());
        if (req.getProveedorId() != null) gasto.setProveedorId(req.getProveedorId());
        if (req.getComprobanteUrl() != null) gasto.setComprobanteUrl(req.getComprobanteUrl());

        List<MovimientoCaja> movimientos = movimientoRepository.findByOrigenAndRefId(OrigenMovimiento.GASTO, id);
        if (!movimientos.isEmpty()) {
            MovimientoCaja mov = movimientos.get(0);
            if (req.getMonto() != null) mov.setMonto(req.getMonto());
            if (req.getMetodoPago() != null) mov.setMetodoPago(req.getMetodoPago());
            if (req.getDescripcion() != null) mov.setDescripcion(req.getDescripcion());
            movimientoRepository.save(mov);
        }

        GastoOperativo updated = gastoRepository.save(gasto);
        return toResponse(updated);
    }

    @Transactional
    public void eliminar(Integer id) {
        GastoOperativo gasto = gastoRepository.findById(id)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.BusinessException(
                        "Gasto no encontrado", "GASTO_NO_ENCONTRADO", org.springframework.http.HttpStatus.NOT_FOUND
                ));
        gasto.setEstado(EstadoGasto.ANULADO);
        gastoRepository.save(gasto);
    }

    private GastoOperativoResponse toResponse(GastoOperativo gasto) {
        return new com.lasa.gloria.cash.application.dto.response.GastoOperativoResponse(
                gasto.getId(),
                gasto.getFecha(),
                gasto.getCategoria(),
                gasto.getDescripcion(),
                gasto.getMonto(),
                gasto.getMetodoPago(),
                gasto.getProveedorId(),
                gasto.getComprobanteUrl(),
                gasto.getUsuarioId(),
                gasto.getCreatedAt()
        );
    }
}