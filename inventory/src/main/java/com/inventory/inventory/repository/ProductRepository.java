package com.inventory.inventory.repository;


import com.inventory.inventory.entity.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(String name, String category);

    List<Product> findByQuantityLessThanEqual(Integer quantity);
}