package com.lasa.gloria.inventory.application.dto.request;

import jakarta.validation.constraints.*;
// import java.math.BigDecimal;

public record AjusteDetalleRequest(
        @NotNull Integer productoId,
        @NotNull @Positive Integer cantidad) {
}
