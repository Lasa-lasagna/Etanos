package com.lasa.gloria.cash.domain.repository;

import com.lasa.gloria.cash.domain.model.CajaDiaria;
import com.lasa.gloria.cash.domain.model.CajaEstado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface CajaDiariaRepository extends JpaRepository<CajaDiaria, Integer> {

    Optional<CajaDiaria> findByFecha(java.time.LocalDate fecha);

    @Query("SELECT c FROM CajaDiaria c WHERE c.estado = com.lasa.gloria.cash.domain.model.CajaEstado.ABIERTA ORDER BY c.fecha DESC")
    Optional<CajaDiaria> findTopByEstadoOrderByFechaDesc();

    Optional<CajaDiaria> findByEstadoAndFecha(CajaEstado estado, LocalDate fecha);

    boolean existsByFecha(LocalDate fecha);
}