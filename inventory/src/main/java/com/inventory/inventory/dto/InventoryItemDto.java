package com.inventory.inventory.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class InventoryItemDto {

    private Long productId;
    private String name;
    private String category;
    private BigDecimal price;
    private Integer quantity;
    private String status;
}