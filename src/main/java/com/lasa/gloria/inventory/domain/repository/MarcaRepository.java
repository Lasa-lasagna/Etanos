package com.lasa.gloria.inventory.domain.repository;

import com.lasa.gloria.inventory.domain.model.Marca;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Integer> {
    List<Marca> findByEstadoTrue();

    Page<Marca> findByEstadoTrue(Pageable pageable);

}
