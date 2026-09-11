package com.lasa.gloria.inventory.domain.repository;

import com.lasa.gloria.inventory.domain.model.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    boolean existsByReferenciaTipoAndReferenciaId(String referenciaTipo, Integer referenciaId);
}
