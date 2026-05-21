package com.inventory.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class SaleResponse {
    private Long saleId;
    private String customerName;
    private BigDecimal totalAmount;
    private LocalDateTime saleDate;
    private List<SaleItemResponse> items;
}