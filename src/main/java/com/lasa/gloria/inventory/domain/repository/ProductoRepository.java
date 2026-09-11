package com.lasa.gloria.inventory.domain.repository;

import com.lasa.gloria.inventory.domain.model.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id IN :ids ORDER BY p.id ASC")
    List<Producto> findByIdInOrderByProductoIdAscWithLock(@Param("ids") List<Integer> ids);

    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.inventario i WHERE p.estado = true ORDER BY p.nombre ASC")
    List<Producto> findAllByEstadoTrueFetchStock();
}
