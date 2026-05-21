package com.inventory.inventory.dto;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class DashboardStatsDto {
    private long totalProducts;
    private long totalSales;
    private long totalCustomers;
    private long lowStockCount;
    private List<SaleResponse> recentTransactions;
    private List<String> revenueLabels;
    private List<BigDecimal> revenueData;
}