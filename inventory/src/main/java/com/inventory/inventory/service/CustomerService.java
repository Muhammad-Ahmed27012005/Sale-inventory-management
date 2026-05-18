package com.inventory.inventory.service;

import com.inventory.inventory.dto.CustomerDto;
import com.inventory.inventory.dto.SaleResponse;
import java.util.List;

public interface CustomerService {

    List<CustomerDto> getAllCustomers();

    CustomerDto createCustomer(CustomerDto customerDto);

    void deleteCustomer(Long id);

    List<CustomerDto> searchCustomers(String query);

    List<SaleResponse> getPurchaseHistory(Long customerId);
}