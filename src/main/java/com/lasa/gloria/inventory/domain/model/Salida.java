package com.lasa.gloria.inventory.domain.model;

import com.lasa.gloria.sales.domain.model.EstadoFactura;
import com.lasa.gloria.sales.domain.model.MetodoPago;
import com.lasa.gloria.sales.domain.model.TipoVenta;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "salidas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Salida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(unique = true, nullable = false, length = 30)
    private String numero;

    @Column(name = "cliente_id")
    private Integer clienteId;

    private Instant fecha;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoDocumento estado = EstadoDocumento.BORRADOR;

    @Column(name = "usuario_id")
    private Integer usuarioId;

    @Column(columnDefinition = "TEXT")
    private String observacion;

    @Version
    private Integer version;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "salida", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("id ASC")
    private List<SalidaDetalle> detalles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_venta", nullable = false, length = 20)
    @Builder.Default
    private TipoVenta tipoVenta = TipoVenta.CONTADO;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 20)
    @Builder.Default
    private MetodoPago metodoPago = MetodoPago.EFECTIVO;

    @Column(name = "entrega_bombona", nullable = false)
    @Builder.Default
    private Boolean entregaBombona = false;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_factura", nullable = false, length = 20)
    @Builder.Default
    private EstadoFactura estadoFactura = EstadoFactura.PENDIENTE;

    @Column(name = "numero_factura", length = 30, unique = true)
    private String numeroFactura;

    @Column(name = "subtotal", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "impuestos", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal impuestos = BigDecimal.ZERO;

    @Column(name = "total", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    public void confirmar() {
        if (this.estado != EstadoDocumento.BORRADOR) {
            throw new IllegalStateException("Solo se pueden confirmar salidas en estado BORRADOR");
        }
        this.estado = EstadoDocumento.CONFIRMADO;
    }

    public void anular() {
        if (this.estado != EstadoDocumento.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden anular salidas en estado CONFIRMADO");
        }
        this.estado = EstadoDocumento.ANULADO;
    }
}