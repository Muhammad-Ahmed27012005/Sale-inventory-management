package com.inventory.inventory.repository;


import com.inventory.inventory.entity.Sale;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    List<Sale> findTop5ByOrderBySaleDateDesc();

    List<Sale> findAllByOrderBySaleDateDesc();

    List<Sale> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    List<Sale> findByCustomerNameIgnoreCaseOrderBySaleDateDesc(String customerName);
}