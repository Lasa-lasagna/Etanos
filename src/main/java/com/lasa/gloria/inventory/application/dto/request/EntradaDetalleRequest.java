package com.lasa.gloria.inventory.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record EntradaDetalleRequest(
        @NotNull Integer productoId,
        @NotNull @Positive Integer cantidad,
        @NotNull @DecimalMin(value = "0.0", inclusive = true) BigDecimal precioCompra) {
}
