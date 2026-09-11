package com.lasa.gloria.cash.application.service;

import com.lasa.gloria.cash.application.dto.request.AperturaCajaRequest;
import com.lasa.gloria.cash.application.dto.request.CierreCajaRequest;
import com.lasa.gloria.cash.application.dto.request.MovimientoCajaRequest;
import com.lasa.gloria.cash.application.dto.response.CajaDiariaResponse;
import com.lasa.gloria.cash.application.dto.response.CajaResumenDiarioResponse;
import com.lasa.gloria.cash.application.dto.response.EstadoCajaResponse;
import com.lasa.gloria.cash.application.dto.response.MovimientoCajaResponse;
import com.lasa.gloria.cash.domain.model.CajaDiaria;
import com.lasa.gloria.cash.domain.model.CajaEstado;

import com.lasa.gloria.cash.domain.model.MovimientoCaja;
import com.lasa.gloria.cash.domain.model.TipoMovimientoCaja;
import com.lasa.gloria.cash.domain.repository.CajaDiariaRepository;
import com.lasa.gloria.cash.domain.repository.MovimientoCajaRepository;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CajaService {

    private final CajaDiariaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoRepository;

    @Transactional
    public CajaDiariaResponse apertura(AperturaCajaRequest req) {
        if (cajaRepository.existsByFecha(LocalDate.now())) {
            throw new com.lasa.gloria.common.exception.BusinessException(
                    "Ya existe una caja abierta para hoy", "CAJA_YA_EXISTE", org.springframework.http.HttpStatus.CONFLICT
            );
        }
        CajaDiaria caja = CajaDiaria.builder()
                .fecha(LocalDate.now())
                .usuarioId(req.usuarioId())
                .montoInicial(req.montoInicial())
                .montoFinalSistema(req.montoInicial())
                .estado(CajaEstado.ABIERTA)
                .build();
        return toResponse(cajaRepository.save(caja));
    }

    @Transactional
    public MovimientoCajaResponse movimiento(Integer cajaId, MovimientoCajaRequest req) {
        CajaDiaria caja = cajaRepository.findById(cajaId)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Caja", cajaId));
        if (caja.getEstado() != CajaEstado.ABIERTA) {
            throw new com.lasa.gloria.common.exception.BusinessException(
                    "Caja no esta ABIERTA", "CAJA_CERRADA", org.springframework.http.HttpStatus.CONFLICT);
        }

        MovimientoCaja mov = MovimientoCaja.builder()
                .cajaDiariaId(caja.getId())
                .tipo(req.tipo())
                .origen(req.origen())
                .refId(req.refId())
                .metodoPago(req.metodoPago())
                .monto(req.monto())
                .descripcion(req.descripcion())
                .build();

        MovimientoCaja saved = movimientoRepository.save(mov);

        // Actualizar totales en caja
        actualizarTotalesCaja(caja.getId());

        return toMovResponse(saved);
    }

    @Transactional
    public CajaDiariaResponse cierre(Integer cajaId, CierreCajaRequest req) {
        CajaDiaria caja = cajaRepository.findById(cajaId)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Caja", cajaId));
        if (caja.getEstado() != CajaEstado.ABIERTA) {
            throw new com.lasa.gloria.common.exception.BusinessException(
                    "Caja no esta ABIERTA", "CAJA_CERRADA", org.springframework.http.HttpStatus.CONFLICT);
        }

        caja.setMontoFinalReal(req.montoFinalReal());
        caja.setEstado(CajaEstado.CERRADA);
        caja.setClosedAt(Instant.now());

        actualizarTotalesCaja(caja.getId());
        caja.setDiferencia(caja.getMontoFinalReal().subtract(caja.getMontoFinalSistema()));

        return toResponse(cajaRepository.save(caja));
    }

    @Transactional(readOnly = true)
    public CajaResumenDiarioResponse resumenDiario(LocalDate fecha) {
        CajaDiaria caja = cajaRepository.findByFecha(fecha)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Caja", fecha.toString()));

        List<Object[]> ingresosPorMetodo = movimientoRepository.sumIngresosPorMetodo(caja.getId());
        List<Object[]> egresosPorMetodo = movimientoRepository.sumEgresosPorMetodo(caja.getId());

        Map<String, BigDecimal> ingresosPorMetodoMap = new LinkedHashMap<>();
        for (Object[] row : ingresosPorMetodo) {
            String metodo = row[0] instanceof MetodoPago m ? m.name() : String.valueOf(row[0]);
            ingresosPorMetodoMap.put(metodo, (java.math.BigDecimal) row[1]);
        }

        Map<String, BigDecimal> egresosPorMetodoMap = new LinkedHashMap<>();
        for (Object[] row : egresosPorMetodo) {
            String metodo = row[0] instanceof MetodoPago m ? m.name() : String.valueOf(row[0]);
            egresosPorMetodoMap.put(metodo, (java.math.BigDecimal) row[1]);
        }

        BigDecimal totalIngresos = movimientoRepository.sumIngresosByCajaId(caja.getId());
        BigDecimal totalEgresos = movimientoRepository.sumEgresosByCajaId(caja.getId());
        BigDecimal montoFinalSistema = caja.getMontoInicial().add(totalIngresos).subtract(totalEgresos);

        return new CajaResumenDiarioResponse(
                caja.getFecha(),
                caja.getMontoInicial(),
                totalIngresos,
                totalEgresos,
                montoFinalSistema,
                caja.getMontoFinalReal(),
                caja.getDiferencia(),
                ingresosPorMetodoMap,
                egresosPorMetodoMap
        );
    }

    @Transactional(readOnly = true)
    public EstadoCajaResponse estado(LocalDate fecha) {
        if (fecha == null) fecha = LocalDate.now();
        Optional<CajaDiaria> opt = cajaRepository.findByFecha(fecha);
        if (opt.isEmpty()) {
            return new EstadoCajaResponse(false, null, null);
        }
        CajaDiaria caja = opt.get();
        boolean abierta = caja.getEstado() == CajaEstado.ABIERTA;
        CajaDiariaResponse cajaResp = toResponse(caja);
        CajaResumenDiarioResponse resumen = resumenDiario(fecha);
        return new EstadoCajaResponse(abierta, cajaResp, resumen);
    }

    @Transactional(readOnly = true)
    public List<MovimientoCajaResponse> movimientosDeCaja(Integer cajaId) {
        CajaDiaria caja = cajaRepository.findById(cajaId)
                .orElseThrow(() -> new com.lasa.gloria.common.exception.NotFoundException("Caja", cajaId));
        return movimientoRepository.findByCajaDiariaId(caja.getId()).stream()
                .map(this::toMovResponse)
                .toList();
    }

    @Transactional
    public void actualizarTotalesCaja(Integer cajaId) {
        CajaDiaria caja = cajaRepository.findById(cajaId).orElseThrow();
        BigDecimal totalIngresos = movimientoRepository.sumIngresosByCajaId(cajaId);
        BigDecimal totalEgresos = movimientoRepository.sumEgresosByCajaId(cajaId);
        caja.setMontoFinalSistema(caja.getMontoInicial().add(totalIngresos).subtract(totalEgresos));
    }

    private com.lasa.gloria.cash.application.dto.response.CajaDiariaResponse toResponse(CajaDiaria caja) {
        return new CajaDiariaResponse(
                caja.getId(),
                caja.getFecha(),
                caja.getUsuarioId(),
                caja.getMontoInicial(),
                caja.getMontoFinalSistema(),
                caja.getMontoFinalReal(),
                caja.getDiferencia(),
                caja.getEstado(),
                caja.getOpenedAt(),
                caja.getClosedAt()
        );
    }

    private com.lasa.gloria.cash.application.dto.response.MovimientoCajaResponse toMovResponse(MovimientoCaja mov) {
        return new MovimientoCajaResponse(
                mov.getId(),
                mov.getCajaDiariaId(),
                mov.getTipo(),
                mov.getOrigen(),
                mov.getRefId(),
                mov.getMetodoPago(),
                mov.getMonto(),
                mov.getDescripcion(),
                mov.getFecha()
        );
    }
}
