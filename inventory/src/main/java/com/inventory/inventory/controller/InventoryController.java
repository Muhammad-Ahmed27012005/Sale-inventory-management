package com.inventory.inventory.controller;

import com.inventory.inventory.dto.InventoryItemDto;
import com.inventory.inventory.dto.InventoryUpdateRequest;
import com.inventory.inventory.service.InventoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    public ResponseEntity<List<InventoryItemDto>> getInventory() {
        return ResponseEntity.ok(inventoryService.getInventory());
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItemDto>> getLowStock() {
        return ResponseEntity.ok(inventoryService.getLowStock());
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<InventoryItemDto> updateStock(@PathVariable Long id,
            @Valid @RequestBody InventoryUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.updateStock(id, request.getQuantity()));
    }
}