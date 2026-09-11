package com.lasa.gloria.partners.application.dto.request;

import com.lasa.gloria.partners.domain.model.TipoDocumento;
import jakarta.validation.constraints.*;
// import java.math.BigDecimal;

public record CrearProveedorRequest(
        @NotNull TipoDocumento tipoDoc,
        @NotBlank @Size(max = 20) String nroDoc,
        @NotBlank @Size(max = 150) String razonSocial,
        @Size(max = 150) String nombreComercial,
        @Size(max = 20) String telefono,
        @Size(max = 255) String direccion,
        @Min(0) Integer condicionPago
) {}