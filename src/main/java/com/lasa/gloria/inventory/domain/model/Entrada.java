package com.lasa.gloria.inventory.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
// import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entradas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Entrada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(unique = true, nullable = false, length = 30)
    private String numero;

    @Column(name = "proveedor_id")
    private Integer proveedorId;

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

    @OneToMany(mappedBy = "entrada", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("id ASC")
    private List<EntradaDetalle> detalles = new ArrayList<>();

    public void confirmar() {
        if (this.estado != EstadoDocumento.BORRADOR) {
            throw new IllegalStateException("Solo se pueden confirmar entradas en estado BORRADOR");
        }
        this.estado = EstadoDocumento.CONFIRMADO;
    }

    public void anular() {
        if (this.estado != EstadoDocumento.CONFIRMADO) {
            throw new IllegalStateException("Solo se pueden anular entradas en estado CONFIRMADO");
        }
        this.estado = EstadoDocumento.ANULADO;
    }
}
