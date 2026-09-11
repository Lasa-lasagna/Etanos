package com.lasa.gloria.sales.application.dto.response;

import com.lasa.gloria.inventory.application.dto.response.SalidaDetalleResponse;
import com.lasa.gloria.sales.domain.model.TipoVenta;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import com.lasa.gloria.sales.domain.model.EstadoFactura;
import com.lasa.gloria.inventory.domain.model.EstadoDocumento;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record VentaDetalleResponse(
        Integer id,
        Integer productoId,
        String productoNombre,
        Integer cantidad,
        BigDecimal precioVenta,
        BigDecimal subtotal
) {}