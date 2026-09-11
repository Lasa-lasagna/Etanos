package com.lasa.gloria.inventory.domain.repository;

import com.lasa.gloria.inventory.domain.model.Ajuste;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface AjusteRepository extends JpaRepository<Ajuste, Integer> {

    Page<Ajuste> findByFechaBetween(Instant desde, Instant hasta, Pageable pageable);

    @Query(value = "select nextval('ajustes_seq')", nativeQuery = true)
    long nextNumero();
}
