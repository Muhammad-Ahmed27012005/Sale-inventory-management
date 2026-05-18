package com.inventory.inventory.repository;


import com.inventory.inventory.entity.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(String name, String phone);
}