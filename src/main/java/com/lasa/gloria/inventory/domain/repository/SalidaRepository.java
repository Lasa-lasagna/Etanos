package com.lasa.gloria.inventory.domain.repository;

import com.lasa.gloria.inventory.domain.model.Salida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface SalidaRepository extends JpaRepository<Salida, Integer> {

    Page<Salida> findByFechaBetween(Instant desde, Instant hasta, Pageable pageable);

    Page<Salida> findByTipoVentaAndEstadoFactura(com.lasa.gloria.sales.domain.model.TipoVenta tipoVenta, com.lasa.gloria.sales.domain.model.EstadoFactura estadoFactura, Pageable pageable);

    @Query(value = "select nextval('salidas_seq')", nativeQuery = true)
    long nextNumero();
}
