package com.lasa.gloria.sales.application.dto.request;

import com.lasa.gloria.inventory.application.dto.request.SalidaDetalleRequest;
import com.lasa.gloria.sales.domain.model.TipoVenta;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

public record CrearVentaRequest(
        @NotNull Integer clienteId,
        @NotNull Integer usuarioId,
        @NotNull TipoVenta tipoVenta,
        @NotNull MetodoPago metodoPago,
        Boolean entregaBombona,
        LocalDate fechaVencimiento,
        @NotEmpty @Valid List<SalidaDetalleRequest> detalles
) {}