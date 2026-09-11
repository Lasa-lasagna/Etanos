package com.lasa.gloria.partners.application.dto.request;

import com.lasa.gloria.partners.domain.model.TipoDocumento;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record CrearClienteRequest(
        @NotNull TipoDocumento tipoDoc,
        @NotBlank @Size(max = 20) String nroDoc,
        @NotBlank @Size(max = 150) String nombre,
        @Size(max = 20) String telefono,
        @Size(max = 255) String direccion,
        @DecimalMin(value = "0.0", inclusive = true) BigDecimal limiteCredito,
        @Min(0) Integer diasCredito
) {}