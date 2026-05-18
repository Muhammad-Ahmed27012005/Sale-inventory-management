package com.inventory.inventory.service;

import com.inventory.inventory.dto.InventoryItemDto;
import java.util.List;

public interface InventoryService {

    List<InventoryItemDto> getInventory();

    List<InventoryItemDto> getLowStock();

    InventoryItemDto updateStock(Long productId, Integer quantity);
}