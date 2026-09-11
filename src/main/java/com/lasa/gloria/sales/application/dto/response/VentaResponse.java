package com.lasa.gloria.sales.application.dto.response;

import com.lasa.gloria.sales.domain.model.EstadoFactura;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import com.lasa.gloria.sales.domain.model.TipoVenta;
import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record VentaResponse(
        Integer id,
        String numero,
        Integer clienteId,
        String clienteNombre,
        EstadoDocumento estado,
        TipoVenta tipoVenta,
        MetodoPago metodoPago,
        Boolean entregaBombona,
        LocalDate fechaVencimiento,
        EstadoFactura estadoFactura,
        String numeroFactura,
        BigDecimal subtotal,
        BigDecimal impuestos,
        BigDecimal total,
        Instant fecha,
        List<VentaDetalleResponse> detalles
) {}
