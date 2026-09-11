package com.lasa.gloria.inventory.domain.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ValuationService {

    public static final int SCALE = 6;

    public BigDecimal calcularNuevoCostoPromedio(
            int stockActual,
            BigDecimal costoPromedioActual,
            int cantidadEntrada,
            BigDecimal costoEntrada) {
        BigDecimal valorActual = costoPromedioActual.multiply(BigDecimal.valueOf(stockActual));
        BigDecimal valorEntrada = costoEntrada.multiply(BigDecimal.valueOf(cantidadEntrada));
        int stockNuevo = stockActual + cantidadEntrada;
        if (stockNuevo == 0) {
            return BigDecimal.ZERO.setScale(SCALE);
        }
        return valorActual.add(valorEntrada)
                .divide(BigDecimal.valueOf(stockNuevo), SCALE, RoundingMode.HALF_UP);
    }
}
