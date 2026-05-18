package com.inventory.inventory.service;

import com.inventory.inventory.dto.SaleRequest;
import com.inventory.inventory.dto.SaleResponse;
import java.util.List;

public interface SalesService {

    SaleResponse createSale(SaleRequest saleRequest);

    List<SaleResponse> getAllSales();

    SaleResponse getSaleById(Long id);
}