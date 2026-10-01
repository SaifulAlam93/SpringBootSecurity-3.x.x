package com.abc.SpringBootSecqurityEx.repository;

import com.abc.SpringBootSecqurityEx.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByActiveTrueOrderByNameAsc();

    List<ProductEntity> findAllByPremiumTrueAndActiveTrueOrderByNameAsc();

    long countByActiveTrue();

    long countByActiveFalse();

    long countByStockLessThan(int stock);

    long countByPremiumTrueAndActiveTrue();
}
