package com.lasa.gloria.partners.application.dto.response;

import com.lasa.gloria.partners.domain.model.TipoDocumento;
import java.math.BigDecimal;
// import java.time.LocalDateTime;

public record ClienteResponse(
        Integer id,
        TipoDocumento tipoDoc,
        String nroDoc,
        String nombre,
        String telefono,
        String direccion,
        BigDecimal limiteCredito,
        Integer diasCredito
        //tampoco aqui
        // Boolean estado,
        // LocalDateTime createdAt,
        // LocalDateTime updatedAt
) {}