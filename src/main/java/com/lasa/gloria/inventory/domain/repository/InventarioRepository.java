package com.lasa.gloria.inventory.domain.repository;

import com.lasa.gloria.inventory.domain.model.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Integer> {

    Optional<Inventario> findByProductoId(Integer productoId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Inventario i WHERE i.producto.id IN :productoIds ORDER BY i.producto.id ASC")
    List<Inventario> findByProductoIdInOrderByProductoIdAscWithLock(@Param("productoIds") List<Integer> productoIds);
}
