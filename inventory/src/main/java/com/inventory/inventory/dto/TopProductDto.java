package com.inventory.inventory.dto;


import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class TopProductDto {

    private Long productId;
    private String productName;
    private Long quantitySold;
    private BigDecimal revenue;
}