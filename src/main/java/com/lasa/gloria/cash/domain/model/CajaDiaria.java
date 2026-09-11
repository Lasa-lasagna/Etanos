package com.lasa.gloria.cash.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "cajas_diarias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CajaDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(nullable = false, unique = true)
    private LocalDate fecha;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "monto_inicial", precision = 18, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal montoInicial = java.math.BigDecimal.ZERO;

    @Column(name = "monto_final_sistema", precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal montoFinalSistema = java.math.BigDecimal.ZERO;

    @Column(name = "monto_final_real", precision = 18, scale = 2)
    private BigDecimal montoFinalReal;

    @Column(precision = 18, scale = 2)
    private BigDecimal diferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private CajaEstado estado = CajaEstado.ABIERTA;

    @CreationTimestamp
    @Column(name = "opened_at", updatable = false)
    private Instant openedAt;

    @Column(name = "closed_at")
    private Instant closedAt;
}