package com.inventory.inventory.controller;

import com.inventory.inventory.dto.SaleRequest;
import com.inventory.inventory.dto.SaleResponse;
import com.inventory.inventory.service.SalesService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;

    @PostMapping
    public ResponseEntity<SaleResponse> createSale(@Valid @RequestBody SaleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesService.createSale(request));
    }

    @GetMapping
    public ResponseEntity<List<SaleResponse>> getSales() {
        return ResponseEntity.ok(salesService.getAllSales());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SaleResponse> getSale(@PathVariable Long id) {
        return ResponseEntity.ok(salesService.getSaleById(id));
    }
} // Fixed: Removed extra closing brace