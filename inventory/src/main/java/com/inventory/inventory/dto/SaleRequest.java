package com.inventory.inventory.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class SaleRequest {
    @NotBlank(message = "Customer name is required")
    private String customerName;

    @Valid
    @NotEmpty(message = "At least one sale item is required")
    private List<SaleItemRequest> items;
}