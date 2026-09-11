package com.lasa.gloria.cash.application.dto.request;

import com.lasa.gloria.sales.domain.model.MetodoPago;
import jakarta.annotation.Nullable;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ActualizarGastoRequest {
    @Nullable private LocalDate fecha;
    @Nullable private String categoria;
    @Nullable private String descripcion;
    @Nullable private BigDecimal monto;
    @Nullable private MetodoPago metodoPago;
    @Nullable private Integer proveedorId;
    @Nullable private String comprobanteUrl;
}
