package com.lasa.gloria.partners.domain.repository;

import com.lasa.gloria.partners.domain.model.Cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByNroDoc(String nroDoc);

    Page<Cliente> findByEstadoTrue(Pageable pageable);

    java.util.List<Cliente> findByEstadoTrue();

    @Query("SELECT c FROM Cliente c WHERE c.estado = true AND (LOWER(c.nombre) LIKE LOWER(CONCAT('%', :term, '%')) OR c.nroDoc LIKE CONCAT('%', :term, '%'))")
    Page<Cliente> buscar(@Param("term") String term, Pageable pageable);

    boolean existsByNroDoc(String nroDoc);
}