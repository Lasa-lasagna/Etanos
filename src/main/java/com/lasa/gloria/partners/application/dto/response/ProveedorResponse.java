package com.lasa.gloria.partners.application.dto.response;

import com.lasa.gloria.partners.domain.model.TipoDocumento;
// import java.time.LocalDateTime;

public record ProveedorResponse(
        Integer id,
        TipoDocumento tipoDoc,
        String nroDoc,
        String razonSocial,
        String nombreComercial,
        String telefono,
        String direccion,
        Integer condicionPago
        //no debe ir el estado ni el tiempo de creacion en la respesta, vaya vibecodeada
        // Boolean estado,
        // LocalDateTime createdAt,
        // LocalDateTime updatedAt
) {}