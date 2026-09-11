package com.lasa.gloria.inventory.application.dto.request;

import jakarta.validation.constraints.*;
// import java.math.BigDecimal;

public record CrearMarcaRequest(
        @NotBlank @Size(max = 100) String nombre,
        @Size(max = 255) String descripcion) {
}
