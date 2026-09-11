package com.lasa.gloria.partners.application.dto.request;

import jakarta.validation.constraints.*;

public record ActualizarProveedorRequest(
        @Size(max = 20) String telefono,
        @Size(max = 255) String direccion,
        @Min(0) Integer condicionPago,
        Boolean estado
) {}