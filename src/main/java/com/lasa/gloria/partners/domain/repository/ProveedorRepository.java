package com.lasa.gloria.partners.domain.repository;

import com.lasa.gloria.partners.domain.model.Proveedor;
import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    Optional<Proveedor> findByNroDoc(String nroDoc);

    @Query("SELECT p FROM Proveedor p WHERE p.estado = true AND (LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :term, '%')) OR LOWER(p.nombreComercial) LIKE LOWER(CONCAT('%', :term, '%')) OR p.nroDoc LIKE CONCAT('%', :term, '%'))")
    Page<Proveedor> buscar(@Param("term") String term, org.springframework.data.domain.Pageable pageable);

    boolean existsByNroDoc(String nroDoc);
}