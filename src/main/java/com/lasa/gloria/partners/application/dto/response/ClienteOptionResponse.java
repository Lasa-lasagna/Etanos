package com.lasa.gloria.partners.application.dto.response;

import com.lasa.gloria.partners.domain.model.TipoDocumento;
import java.math.BigDecimal;

public record ClienteOptionResponse(
        Integer id,
        TipoDocumento tipoDoc,
        String nroDoc,
        String nombre,
        String telefono,
        String direccion,
        BigDecimal limiteCredito,
        Boolean estado
) {}
