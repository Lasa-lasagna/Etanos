package com.lasa.gloria.sales.application.service;

import com.lasa.gloria.cash.domain.model.CajaDiaria;
import com.lasa.gloria.cash.domain.model.CajaEstado;
import com.lasa.gloria.cash.domain.model.MovimientoCaja;
import com.lasa.gloria.cash.domain.model.OrigenMovimiento;
import com.lasa.gloria.cash.domain.model.TipoMovimientoCaja;
import com.lasa.gloria.cash.domain.repository.CajaDiariaRepository;
import com.lasa.gloria.cash.domain.repository.MovimientoCajaRepository;
import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.partners.domain.model.Cliente;
import com.lasa.gloria.partners.domain.repository.ClienteRepository;
import com.lasa.gloria.sales.application.dto.request.CrearVentaRequest;
import com.lasa.gloria.sales.application.dto.response.VentaDetalleResponse;
import com.lasa.gloria.sales.application.dto.response.VentaResponse;
import com.lasa.gloria.sales.application.dto.response.VentasPendientesResponse;
import com.lasa.gloria.sales.application.mapper.VentaMapper;
import com.lasa.gloria.sales.domain.model.Cobro;
import com.lasa.gloria.sales.domain.model.EstadoFactura;
import com.lasa.gloria.sales.domain.model.TipoVenta;
import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import com.lasa.gloria.inventory.domain.model.Salida;
import com.lasa.gloria.inventory.domain.model.SalidaDetalle;
import com.lasa.gloria.inventory.domain.model.Producto;
import com.lasa.gloria.inventory.domain.repository.SalidaRepository;
import com.lasa.gloria.inventory.domain.repository.ProductoRepository;
import com.lasa.gloria.inventory.domain.service.StockService;
import com.lasa.gloria.sales.domain.repository.CobroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final SalidaRepository salidaRepository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final StockService stockService;
    private final VentaMapper mapper;
    private final CajaDiariaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoRepository;
    private final CobroRepository cobroRepository;

    @Transactional
    public VentaResponse crear(CrearVentaRequest req) {
        if (req.tipoVenta() == TipoVenta.CREDITO) {
            if (req.fechaVencimiento() == null) {
                throw new BusinessException("Fecha de vencimiento obligatoria para ventas a crédito", "FECHA_VENCIMIENTO_REQUERIDA", org.springframework.http.HttpStatus.BAD_REQUEST);
            }
            // Validar límite de crédito
            // TODO: implementar validación de límite de crédito
        }

        // Validar caja abierta para CONTADO
        if (req.tipoVenta() == TipoVenta.CONTADO) {
            CajaDiaria caja = cajaRepository.findByEstadoAndFecha(CajaEstado.ABIERTA, LocalDate.now())
                    .orElseThrow(() -> new BusinessException("No hay caja abierta para hoy", "CAJA_NO_ABIERTA", org.springframework.http.HttpStatus.CONFLICT));
        }

        Salida salida = Salida.builder()
                .numero(generarNumero())
                .clienteId(req.clienteId())
                .usuarioId(req.usuarioId())
                .fecha(Instant.now())
                .estado(EstadoDocumento.BORRADOR)
                .tipoVenta(req.tipoVenta())
                .metodoPago(req.metodoPago())
                .entregaBombona(req.entregaBombona() != null ? req.entregaBombona() : false)
                .fechaVencimiento(req.fechaVencimiento())
                .estadoFactura(EstadoFactura.PENDIENTE)
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

        // Calcular totales
        BigDecimal subtotal = detalles.stream().map(SalidaDetalle::getSubtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal; // impuestos ya incluidos en precio
        salida.setSubtotal(subtotal);
        salida.setImpuestos(BigDecimal.ZERO);
        salida.setTotal(total);

        return mapper.toResponse(salidaRepository.save(salida), clienteRepository.findById(req.clienteId()).map(Cliente::getNombre).orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<VentaResponse> listar(Pageable pageable) {
        return salidaRepository.findAll(pageable).map(salida -> {
            String nombre = clienteRepository.findById(salida.getClienteId()).map(Cliente::getNombre).orElse(null);
            return mapper.toResponse(salida, nombre);
        });
    }

    @Transactional(readOnly = true)
    public VentaResponse obtener(Integer id) {
        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta", id));
        return mapper.toResponse(salida, clienteRepository.findById(salida.getClienteId()).map(Cliente::getNombre).orElse(null));
    }

    @Transactional
    public VentaResponse confirmar(Integer id, Boolean entregaBombona) {
        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta", id));

        if (salida.getEstado() != EstadoDocumento.BORRADOR) {
            throw new BusinessException("Venta no esta en estado BORRADOR", "VENTA_NO_CONFIRMABLE", org.springframework.http.HttpStatus.CONFLICT);
        }

        if (salida.getTipoVenta() == TipoVenta.CREDITO) {
            // Validar límite de crédito del cliente
            // TODO: implementar
        }

        // CONTADO requiere caja abierta al momento de confirmar (cobro inmediato)
        CajaDiaria caja = null;
        if (salida.getTipoVenta() == TipoVenta.CONTADO) {
            caja = cajaRepository.findByEstadoAndFecha(CajaEstado.ABIERTA, LocalDate.now())
                    .orElseThrow(() -> new BusinessException("No hay caja abierta para hoy", "CAJA_NO_ABIERTA", org.springframework.http.HttpStatus.CONFLICT));
        }

        stockService.procesarSalida(salida);
        salida.confirmar();

        // reescribir entregaBombona si viene en confirmar (intercambio en persona)
        if (entregaBombona != null) {
            salida.setEntregaBombona(entregaBombona);
        }

        if (salida.getTipoVenta() == TipoVenta.CREDITO) {
            salida.setEstadoFactura(EstadoFactura.EMITIDA);
        } else {
            salida.setEstadoFactura(EstadoFactura.COBRADA);
        }

        Salida saved = salidaRepository.save(salida);

        // CONTADO: registrar ingreso en caja inmediatamente
        if (salida.getTipoVenta() == TipoVenta.CONTADO) {
            MovimientoCaja mov = MovimientoCaja.builder()
                    .cajaDiariaId(caja.getId())
                    .tipo(com.lasa.gloria.cash.domain.model.TipoMovimientoCaja.INGRESO)
                    .origen(com.lasa.gloria.cash.domain.model.OrigenMovimiento.VENTA)
                    .refId(saved.getId())
                    .metodoPago(saved.getMetodoPago())
                    .monto(saved.getTotal())
                    .descripcion("Venta contado " + saved.getNumero())
                    .build();
            movimientoRepository.save(mov);
        }

        return mapper.toResponse(saved, clienteRepository.findById(saved.getClienteId()).map(Cliente::getNombre).orElse(null));
    }

    @Transactional
    public VentaResponse anular(Integer id) {
        Salida salida = salidaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Venta", id));
        if (salida.getEstado() != EstadoDocumento.CONFIRMADO) {
            throw new BusinessException("Solo se pueden anular ventas CONFIRMADAS", "VENTA_NO_ANULABLE", org.springframework.http.HttpStatus.CONFLICT);
        }

        // Revertir movimientos de caja
        CajaDiaria caja = cajaRepository.findByEstadoAndFecha(CajaEstado.ABIERTA, LocalDate.now())
                .orElseThrow(() -> new BusinessException("No hay caja abierta para hoy", "CAJA_NO_ABIERTA", org.springframework.http.HttpStatus.CONFLICT));
        if (salida.getTipoVenta() == TipoVenta.CONTADO) {
            List<MovimientoCaja> ingreVentas = movimientoRepository.findByOrigenAndRefId(OrigenMovimiento.VENTA, salida.getId());
            for (MovimientoCaja inm : ingreVentas) {
                MovimientoCaja rev = MovimientoCaja.builder()
                        .cajaDiariaId(caja.getId())
                        .tipo(TipoMovimientoCaja.EGRESO)
                        .origen(OrigenMovimiento.OTRO)
                        .refId(salida.getId())
                        .metodoPago(inm.getMetodoPago())
                        .monto(inm.getMonto())
                        .descripcion("Anulación venta " + salida.getNumero())
                        .build();
                movimientoRepository.save(rev);
            }
        }
        if (salida.getTipoVenta() == TipoVenta.CREDITO) {
            List<Cobro> cobros = cobroRepository.findByVentaId(id);
            for (Cobro cobro : cobros) {
                List<MovimientoCaja> ingreCobros = movimientoRepository.findByOrigenAndRefId(OrigenMovimiento.COBRO, cobro.getId());
                for (MovimientoCaja inm : ingreCobros) {
                    MovimientoCaja rev = MovimientoCaja.builder()
                            .cajaDiariaId(caja.getId())
                            .tipo(TipoMovimientoCaja.EGRESO)
                            .origen(OrigenMovimiento.OTRO)
                            .refId(cobro.getId())
                            .metodoPago(inm.getMetodoPago())
                            .monto(inm.getMonto())
                            .descripcion("Anulación cobro venta " + salida.getNumero())
                            .build();
                    movimientoRepository.save(rev);
                }
            }
        }

        // Reversar stock
        stockService.reversarSalida(salida);
        salida.anular();
        salida.setEstadoFactura(EstadoFactura.ANULADA);
        return mapper.toResponse(salidaRepository.save(salida), clienteRepository.findById(salida.getClienteId()).map(Cliente::getNombre).orElse(null));
    }

    @Transactional(readOnly = true)
    public Page<VentasPendientesResponse> listarCreditoPendiente(Pageable pageable) {
        return salidaRepository.findByTipoVentaAndEstadoFactura(TipoVenta.CREDITO, EstadoFactura.EMITIDA, pageable)
                .map(salida -> {
                    BigDecimal totalCobrado = cobroRepository.sumMontoByVentaId(salida.getId());
                    BigDecimal saldoPendiente = salida.getTotal().subtract(totalCobrado);
                    String nombreCliente = clienteRepository.findById(salida.getClienteId()).get().getNombre();
                    return new VentasPendientesResponse(
                            salida.getId(),
                            salida.getNumero(),
                            salida.getClienteId(),
                            nombreCliente,
                            salida.getEstado(),
                            salida.getTipoVenta(),
                            salida.getMetodoPago(),
                            salida.getEntregaBombona(),
                            salida.getFechaVencimiento(),
                            salida.getEstadoFactura(),
                            salida.getNumeroFactura(),
                            salida.getSubtotal(),
                            salida.getImpuestos(),
                            salida.getTotal(),
                            saldoPendiente,
                            salida.getFecha(),
                            salida.getDetalles().stream().map(d -> new VentaDetalleResponse(d.getId(), d.getProducto().getId(), d.getProducto().getNombre(), d.getCantidad(), d.getPrecioVenta(), d.getSubtotal())).toList()
                    );
                });
    }

    private String generarNumero() {
        return "V-" + String.format("%06d", salidaRepository.nextNumero());
    }
}