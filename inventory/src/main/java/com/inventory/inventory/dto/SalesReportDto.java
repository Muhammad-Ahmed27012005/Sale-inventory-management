package com.inventory.inventory.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SalesReportDto {
    private String period;
    private long totalSales;
    private BigDecimal totalRevenue;
}