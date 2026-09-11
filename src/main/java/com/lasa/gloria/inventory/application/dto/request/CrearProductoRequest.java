package com.lasa.gloria.inventory.application.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CrearProductoRequest(
        @NotNull Integer marcaId,
        @NotBlank @Size(max = 150) String nombre,
        String descripcion,
        @DecimalMin("0.00") BigDecimal precioCompra,
        @DecimalMin("0.00") BigDecimal precioVenta) {
}
