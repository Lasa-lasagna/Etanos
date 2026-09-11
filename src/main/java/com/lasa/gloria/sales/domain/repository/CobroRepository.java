package com.lasa.gloria.sales.domain.repository;

import com.lasa.gloria.sales.domain.model.Cobro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface CobroRepository extends JpaRepository<Cobro, Integer> {

    @Query("SELECT c FROM Cobro c WHERE c.ventaId = :ventaId ORDER BY c.fecha ASC")
    List<Cobro> findByVentaId(@Param("ventaId") Integer ventaId);

    @Query("SELECT COALESCE(SUM(c.monto), 0) FROM Cobro c WHERE c.ventaId = :ventaId")
    BigDecimal sumMontoByVentaId(@Param("ventaId") Integer ventaId);
}