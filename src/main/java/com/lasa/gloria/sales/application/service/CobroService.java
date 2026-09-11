package com.lasa.gloria.sales.application.service;

import com.lasa.gloria.cash.domain.model.CajaDiaria;
import com.lasa.gloria.cash.domain.model.CajaEstado;
import com.lasa.gloria.cash.domain.model.MovimientoCaja;
import com.lasa.gloria.cash.domain.repository.CajaDiariaRepository;
import com.lasa.gloria.cash.domain.repository.MovimientoCajaRepository;
import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
// import com.lasa.gloria.partners.domain.repository.ClienteRepository;
import com.lasa.gloria.sales.application.dto.request.RegistrarCobroRequest;
import com.lasa.gloria.sales.application.dto.response.CobroResponse;
import com.lasa.gloria.sales.domain.model.Cobro;
import com.lasa.gloria.sales.domain.model.EstadoFactura;
// import com.lasa.gloria.sales.domain.model.MetodoPago;
import com.lasa.gloria.sales.domain.repository.CobroRepository;
import com.lasa.gloria.inventory.domain.model.Salida;
import com.lasa.gloria.inventory.domain.repository.SalidaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CobroService {

    private final CobroRepository cobroRepository;
    private final SalidaRepository salidaRepository;
    // private final ClienteRepository clienteRepository;
    private final CajaDiariaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoRepository;

    @Transactional
    public CobroResponse registrarCobro(RegistrarCobroRequest req) {
        Salida venta = salidaRepository.findById(req.ventaId())
                .orElseThrow(() -> new NotFoundException("Venta", req.ventaId()));

        if (venta.getTipoVenta() != com.lasa.gloria.sales.domain.model.TipoVenta.CREDITO) {
            throw new BusinessException("Solo se pueden registrar cobros en ventas a crédito", "TIPO_VENTA_INVALIDO", org.springframework.http.HttpStatus.BAD_REQUEST);
        }
        if (venta.getEstado() != com.lasa.gloria.inventory.domain.model.EstadoDocumento.CONFIRMADO) {
            throw new BusinessException("Solo se pueden cobrar ventas CONFIRMADAS", "VENTA_NO_CONFIRMADA", org.springframework.http.HttpStatus.CONFLICT);
        }
        if (venta.getEstadoFactura() == EstadoFactura.ANULADA || venta.getEstadoFactura() == EstadoFactura.COBRADA) {
            throw new BusinessException("Venta ya cobrada o anulada", "VENTA_NO_COBRABLE", org.springframework.http.HttpStatus.CONFLICT);
        }

        CajaDiaria caja = cajaRepository.findByEstadoAndFecha(CajaEstado.ABIERTA, LocalDate.now())
                .orElseThrow(() -> new BusinessException("No hay caja abierta", "CAJA_NO_ABIERTA", org.springframework.http.HttpStatus.CONFLICT));

        BigDecimal cobradoTotal = cobroRepository.sumMontoByVentaId(venta.getId());
        BigDecimal saldoPendiente = venta.getTotal().subtract(cobradoTotal);

        if (req.monto().compareTo(saldoPendiente) > 0) {
            throw new BusinessException("El monto excede el saldo pendiente", "MONTO_EXCEDE_SALDO", org.springframework.http.HttpStatus.BAD_REQUEST);
        }

        Cobro cobro = Cobro.builder()
                .ventaId(venta.getId())
                .monto(req.monto())
                .metodoPago(req.metodoPago())
                .referencia(req.referencia())
                .observacion(req.observacion())
                .usuarioId(req.usuarioId())
                .build();

        Cobro saved = cobroRepository.save(cobro);

        // Actualizar estado_factura si se completó el pago (derivado de cobros reales)
        BigDecimal nuevoCobrado = cobradoTotal.add(req.monto());
        if (nuevoCobrado.compareTo(venta.getTotal()) >= 0) {
            venta.setEstadoFactura(EstadoFactura.COBRADA);
        } else {
            venta.setEstadoFactura(EstadoFactura.EMITIDA);
        }
        salidaRepository.save(venta);

        // Registrar movimiento en caja
        MovimientoCaja mov = MovimientoCaja.builder()
                .cajaDiariaId(caja.getId())
                .tipo(com.lasa.gloria.cash.domain.model.TipoMovimientoCaja.INGRESO)
                .origen(com.lasa.gloria.cash.domain.model.OrigenMovimiento.COBRO)
                .refId(saved.getId())
                .metodoPago(req.metodoPago())
                .monto(req.monto())
                .descripcion("Cobro venta " + venta.getNumero())
                .build();
        movimientoRepository.save(mov);

        return toResponse(saved, venta);
    }

    private CobroResponse toResponse(Cobro cobro, Salida venta) {
        return new CobroResponse(
                cobro.getId(),
                cobro.getVentaId(),
                venta.getNumero(),
                cobro.getFecha(),
                cobro.getMonto(),
                cobro.getMetodoPago(),
                cobro.getReferencia(),
                cobro.getObservacion()
        );
    }
}