package com.lasa.gloria.inventory.application.dto.response;

import java.math.BigDecimal;

public record ProductoOptionResponse(
        Integer id,
        String nombre,
        BigDecimal precioVenta,
        Integer stockActual
) {}
