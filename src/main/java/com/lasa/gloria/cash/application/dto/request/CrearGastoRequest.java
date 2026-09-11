package com.lasa.gloria.cash.application.dto.request;

import com.lasa.gloria.sales.domain.model.MetodoPago;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public record CrearGastoRequest(
        @NotNull LocalDate fecha,
        @NotBlank @Size(max = 50) String categoria,
        @Size(max = 255) String descripcion,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal monto,
        @NotNull MetodoPago metodoPago,
        Integer proveedorId,
        @Size(max = 500) String comprobanteUrl,
        Integer usuarioId
) {}