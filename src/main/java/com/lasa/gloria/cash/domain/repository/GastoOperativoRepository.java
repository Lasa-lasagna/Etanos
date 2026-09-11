package com.lasa.gloria.cash.domain.repository;

import com.lasa.gloria.cash.domain.model.EstadoGasto;
import com.lasa.gloria.cash.domain.model.GastoOperativo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface GastoOperativoRepository extends JpaRepository<GastoOperativo, Integer> {

    @Query("SELECT g FROM GastoOperativo g WHERE g.fecha BETWEEN :inicio AND :fin ORDER BY g.fecha ASC")
    List<GastoOperativo> findByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    Page<GastoOperativo> findByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin, Pageable pageable);

    @Query("SELECT COALESCE(SUM(g.monto), 0) FROM GastoOperativo g WHERE g.fecha BETWEEN :inicio AND :fin")
    BigDecimal sumMontoByFechaBetween(@Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    @Query("SELECT g FROM GastoOperativo g WHERE g.categoria = :categoria AND g.fecha BETWEEN :inicio AND :fin ORDER BY g.fecha ASC")
    List<GastoOperativo> findByCategoriaAndFechaBetween(@Param("categoria") String categoria, @Param("inicio") LocalDate inicio, @Param("fin") LocalDate fin);

    Page<GastoOperativo> findByCategoria(String categoria, Pageable pageable);

    Page<GastoOperativo> findByEstado(EstadoGasto estado, Pageable pageable);

    Page<GastoOperativo> findByEstadoAndFechaBetween(EstadoGasto estado, LocalDate inicio, LocalDate fin, Pageable pageable);

    Page<GastoOperativo> findByEstadoAndCategoria(EstadoGasto estado, String categoria, Pageable pageable);
}