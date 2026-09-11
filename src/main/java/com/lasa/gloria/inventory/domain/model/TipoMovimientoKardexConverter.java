package com.lasa.gloria.inventory.domain.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TipoMovimientoKardexConverter implements AttributeConverter<TipoMovimientoKardex, String> {

    @Override
    public String convertToDatabaseColumn(TipoMovimientoKardex attribute) {
        if (attribute == null) {
            return null;
        }
        return switch (attribute) {
            case ENTRADA -> "ENTRADA";
            case SALIDA -> "SALIDA";
            case AJUSTE_MAS -> "AJUSTE+";
            case AJUSTE_MENOS -> "AJUSTE-";
        };
    }

    @Override
    public TipoMovimientoKardex convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return switch (dbData) {
            case "ENTRADA" -> TipoMovimientoKardex.ENTRADA;
            case "SALIDA" -> TipoMovimientoKardex.SALIDA;
            case "AJUSTE+" -> TipoMovimientoKardex.AJUSTE_MAS;
            case "AJUSTE-" -> TipoMovimientoKardex.AJUSTE_MENOS;
            default -> throw new IllegalArgumentException("Tipo de movimiento kardex no válido: " + dbData);
        };
    }
}
