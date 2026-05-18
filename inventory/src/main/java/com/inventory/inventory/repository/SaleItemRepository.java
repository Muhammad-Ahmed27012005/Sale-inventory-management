package com.inventory.inventory.repository;


import com.inventory.inventory.entity.SaleItem;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SaleItemRepository extends JpaRepository<SaleItem, Long> {

    @Query("""
            select si.product.productId, si.product.name, sum(si.quantity), sum(si.lineTotal)
            from SaleItem si
            group by si.product.productId, si.product.name
            order by sum(si.quantity) desc
            """)
    List<Object[]> findTopSellingProducts(Pageable pageable);
}