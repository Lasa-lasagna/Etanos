package com.lasa.gloria.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "kardex")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Kardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Convert(converter = TipoMovimientoKardexConverter.class)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimientoKardex tipoMovimiento;

    private Integer cantidad;

    @Column(name = "stock_anterior")
    private Integer stockAnterior;

    @Column(name = "stock_nuevo")
    private Integer stockNuevo;

    @Column(name = "costo_unitario", precision = 18, scale = 6)
    private BigDecimal costoUnitario;

    @Column(name = "referencia_tipo", length = 50)
    private String referenciaTipo;

    @Column(name = "referencia_id")
    private Integer referenciaId;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(length = 255)
    private String observacion;

    @Column
    private Instant fecha;
}
