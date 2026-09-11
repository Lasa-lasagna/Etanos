package com.lasa.gloria.cash.domain.repository;

import com.lasa.gloria.cash.domain.model.MovimientoCaja;
import com.lasa.gloria.cash.domain.model.OrigenMovimiento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Integer> {

    @Query("SELECT m FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId ORDER BY m.fecha ASC")
    List<MovimientoCaja> findByCajaDiariaId(Integer cajaId);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId AND m.tipo = 'INGRESO'")
    BigDecimal sumIngresosByCajaId(Integer cajaId);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId AND m.tipo = 'EGRESO'")
    BigDecimal sumEgresosByCajaId(Integer cajaId);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId AND m.tipo = 'INGRESO' AND m.metodoPago = :metodo")
    BigDecimal sumIngresosByCajaIdAndMetodo(Integer cajaId, String metodo);

    @Query("SELECT COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId AND m.tipo = 'EGRESO' AND m.metodoPago = :metodo")
    BigDecimal sumEgresosByCajaIdAndMetodo(Integer cajaId, String metodo);

    @Query("SELECT m.metodoPago, COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId AND m.tipo = 'INGRESO' GROUP BY m.metodoPago")
    List<Object[]> sumIngresosPorMetodo(Integer cajaId);

    @Query("SELECT m.metodoPago, COALESCE(SUM(m.monto), 0) FROM MovimientoCaja m WHERE m.cajaDiariaId = :cajaId AND m.tipo = 'EGRESO' GROUP BY m.metodoPago")
    List<Object[]> sumEgresosPorMetodo(Integer cajaId);

    @Query("SELECT m FROM MovimientoCaja m WHERE m.origen = :origen AND m.refId = :refId ORDER BY m.fecha ASC")
    List<MovimientoCaja> findByOrigenAndRefId(@Param("origen") OrigenMovimiento origen, @Param("refId") Integer refId);
}