package com.lasa.gloria.inventory.domain.service;

import com.lasa.gloria.common.exception.BusinessException;
import com.lasa.gloria.common.exception.NotFoundException;
import com.lasa.gloria.inventory.domain.model.*;
import com.lasa.gloria.inventory.domain.repository.InventarioRepository;
import com.lasa.gloria.inventory.domain.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final KardexService kardexService;
    private final ValuationService valuationService;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void procesarEntrada(Entrada entrada) {
        List<Integer> productoIds = entrada.getDetalles().stream()
                .map(d -> d.getProducto().getId())
                .distinct().sorted().toList();

        Map<Integer, Inventario> inventarios = inventarioRepository
                .findByProductoIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));
        Map<Integer, Producto> productos = productoRepository
                .findByIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        for (EntradaDetalle det : entrada.getDetalles()) {
            Integer pid = det.getProducto().getId();
            Producto producto = productos.get(pid);
            Inventario inv = inventarios.computeIfAbsent(pid, id -> {
                Inventario nuevo = Inventario.builder().producto(producto).stockActual(0).build();
                return inventarioRepository.save(nuevo);
            });

            int stockAnterior = inv.getStockActual();
            int stockNuevo = stockAnterior + det.getCantidad();
            inv.setStockActual(stockNuevo);

            producto.setCostoPromedio(valuationService.calcularNuevoCostoPromedio(
                    stockAnterior, producto.getCostoPromedio(),
                    det.getCantidad(), det.getPrecioCompra()));

            kardexService.registrar(Kardex.builder()
                    .producto(producto)
                    .tipoMovimiento(TipoMovimientoKardex.ENTRADA)
                    .cantidad(det.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(stockNuevo)
                    .costoUnitario(det.getPrecioCompra())
                    .referenciaTipo("ENTRADA_DETALLE")
                    .referenciaId(det.getId())
                    .usuarioId(entrada.getUsuarioId())
                    .observacion("Entrada #" + entrada.getNumero())
                    .fecha(Instant.now())
                    .build());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void procesarSalida(Salida salida) {
        List<Integer> productoIds = salida.getDetalles().stream()
                .map(d -> d.getProducto().getId())
                .distinct().sorted().toList();
        Map<Integer, Inventario> inventarios = inventarioRepository
                .findByProductoIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));

        for (SalidaDetalle det : salida.getDetalles()) {
            Integer pid = det.getProducto().getId();
            Inventario inv = inventarios.get(pid);
            if (inv == null) {
                throw new NotFoundException("Inventario", pid);
            }
            int stockAnterior = inv.getStockActual();
            if (stockAnterior < det.getCantidad()) {
                throw new BusinessException(
                        "Stock insuficiente para producto " + pid
                                + ": disponible=" + stockAnterior + ", requerido=" + det.getCantidad(),
                        "STOCK_INSUFICIENTE", HttpStatus.CONFLICT);
            }
            int stockNuevo = stockAnterior - det.getCantidad();
            inv.setStockActual(stockNuevo);

            kardexService.registrar(Kardex.builder()
                    .producto(det.getProducto())
                    .tipoMovimiento(TipoMovimientoKardex.SALIDA)
                    .cantidad(det.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(stockNuevo)
                    .costoUnitario(det.getProducto().getCostoPromedio())
                    .referenciaTipo("SALIDA_DETALLE")
                    .referenciaId(det.getId())
                    .usuarioId(salida.getUsuarioId())
                    .observacion("Salida #" + salida.getNumero())
                    .fecha(Instant.now())
                    .build());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void procesarAjuste(Ajuste ajuste) {
        List<Integer> productoIds = ajuste.getDetalles().stream()
                .map(d -> d.getProducto().getId())
                .distinct().sorted().toList();
        Map<Integer, Inventario> inventarios = inventarioRepository
                .findByProductoIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));

        boolean incremento = ajuste.getTipo() == TipoAjuste.INCREMENTO;
        TipoMovimientoKardex tipoKardex = incremento ? TipoMovimientoKardex.AJUSTE_MAS : TipoMovimientoKardex.AJUSTE_MENOS;

        for (AjusteDetalle det : ajuste.getDetalles()) {
            Integer pid = det.getProducto().getId();
            Inventario inv = inventarios.get(pid);
            if (inv == null) {
                throw new NotFoundException("Inventario", pid);
            }
            int stockAnterior = inv.getStockActual();
            int delta = incremento ? det.getCantidad() : -det.getCantidad();
            int stockNuevo = stockAnterior + delta;
            if (stockNuevo < 0) {
                throw new BusinessException(
                        "Ajuste dejaria stock negativo para producto " + pid, "STOCK_NEGATIVO", HttpStatus.CONFLICT);
            }
            inv.setStockActual(stockNuevo);

            kardexService.registrar(Kardex.builder()
                    .producto(det.getProducto())
                    .tipoMovimiento(tipoKardex)
                    .cantidad(det.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(stockNuevo)
                    .costoUnitario(det.getProducto().getCostoPromedio())
                    .referenciaTipo("AJUSTE_DETALLE")
                    .referenciaId(det.getId())
                    .usuarioId(ajuste.getUsuarioId())
                    .observacion("Ajuste #" + ajuste.getNumero() + " - " + ajuste.getMotivo())
                    .fecha(Instant.now())
                    .build());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void reversarEntrada(Entrada entrada) {
        List<Integer> productoIds = entrada.getDetalles().stream()
                .map(d -> d.getProducto().getId())
                .distinct().sorted().toList();
        Map<Integer, Inventario> inventarios = inventarioRepository
                .findByProductoIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));
        Map<Integer, Producto> productos = productoRepository
                .findByIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(Producto::getId, p -> p));

        for (EntradaDetalle det : entrada.getDetalles()) {
            Integer pid = det.getProducto().getId();
            Inventario inv = inventarios.get(pid);
            Producto producto = productos.get(pid);
            int stockAnterior = inv.getStockActual();
            int stockNuevo = stockAnterior - det.getCantidad();
            if (stockNuevo < 0) {
                throw new BusinessException(
                        "Anulacion dejaria stock negativo para producto " + pid, "STOCK_NEGATIVO", HttpStatus.CONFLICT);
            }
            inv.setStockActual(stockNuevo);

            BigDecimal valorActual = producto.getCostoPromedio().multiply(BigDecimal.valueOf(stockAnterior));
            BigDecimal valorEntrada = det.getPrecioCompra().multiply(BigDecimal.valueOf(det.getCantidad()));
            BigDecimal nuevoCosto = stockNuevo == 0
                    ? BigDecimal.ZERO.setScale(ValuationService.SCALE)
                    : valorActual.subtract(valorEntrada)
                            .divide(BigDecimal.valueOf(stockNuevo), ValuationService.SCALE, RoundingMode.HALF_UP);
            producto.setCostoPromedio(nuevoCosto);

            kardexService.registrar(Kardex.builder()
                    .producto(producto)
                    .tipoMovimiento(TipoMovimientoKardex.AJUSTE_MENOS)
                    .cantidad(det.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(stockNuevo)
                    .costoUnitario(det.getPrecioCompra())
                    .referenciaTipo("ANULACION_ENTRADA")
                    .referenciaId(det.getId())
                    .usuarioId(entrada.getUsuarioId())
                    .observacion("Anulacion Entrada #" + entrada.getNumero())
                    .fecha(Instant.now())
                    .build());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void reversarSalida(Salida salida) {
        List<Integer> productoIds = salida.getDetalles().stream()
                .map(d -> d.getProducto().getId())
                .distinct().sorted().toList();
        Map<Integer, Inventario> inventarios = inventarioRepository
                .findByProductoIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));

        for (SalidaDetalle det : salida.getDetalles()) {
            Integer pid = det.getProducto().getId();
            Inventario inv = inventarios.get(pid);
            if (inv == null) {
                throw new NotFoundException("Inventario", pid);
            }
            int stockAnterior = inv.getStockActual();
            int stockNuevo = stockAnterior + det.getCantidad();
            inv.setStockActual(stockNuevo);

            kardexService.registrar(Kardex.builder()
                    .producto(det.getProducto())
                    .tipoMovimiento(TipoMovimientoKardex.AJUSTE_MAS)
                    .cantidad(det.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(stockNuevo)
                    .costoUnitario(det.getProducto().getCostoPromedio())
                    .referenciaTipo("ANULACION_SALIDA")
                    .referenciaId(det.getId())
                    .usuarioId(salida.getUsuarioId())
                    .observacion("Anulacion Salida #" + salida.getNumero())
                    .fecha(Instant.now())
                    .build());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void reversarAjuste(Ajuste ajuste) {
        List<Integer> productoIds = ajuste.getDetalles().stream()
                .map(d -> d.getProducto().getId())
                .distinct().sorted().toList();
        Map<Integer, Inventario> inventarios = inventarioRepository
                .findByProductoIdInOrderByProductoIdAscWithLock(productoIds).stream()
                .collect(Collectors.toMap(i -> i.getProducto().getId(), i -> i));

        boolean incremento = ajuste.getTipo() == TipoAjuste.INCREMENTO;
        TipoMovimientoKardex tipoKardex = incremento ? TipoMovimientoKardex.AJUSTE_MENOS : TipoMovimientoKardex.AJUSTE_MAS;

        for (AjusteDetalle det : ajuste.getDetalles()) {
            Integer pid = det.getProducto().getId();
            Inventario inv = inventarios.get(pid);
            if (inv == null) {
                throw new NotFoundException("Inventario", pid);
            }
            int stockAnterior = inv.getStockActual();
            int delta = incremento ? -det.getCantidad() : det.getCantidad();
            int stockNuevo = stockAnterior + delta;
            if (stockNuevo < 0) {
                throw new BusinessException(
                        "Anulacion dejaria stock negativo para producto " + pid, "STOCK_NEGATIVO", HttpStatus.CONFLICT);
            }
            inv.setStockActual(stockNuevo);

            kardexService.registrar(Kardex.builder()
                    .producto(det.getProducto())
                    .tipoMovimiento(tipoKardex)
                    .cantidad(det.getCantidad())
                    .stockAnterior(stockAnterior)
                    .stockNuevo(stockNuevo)
                    .costoUnitario(det.getProducto().getCostoPromedio())
                    .referenciaTipo("ANULACION_AJUSTE")
                    .referenciaId(det.getId())
                    .usuarioId(ajuste.getUsuarioId())
                    .observacion("Anulacion Ajuste #" + ajuste.getNumero())
                    .fecha(Instant.now())
                    .build());
        }
    }
}
