package com.inventory.inventory.controller;

import com.inventory.inventory.dto.SalesReportDto;
import com.inventory.inventory.dto.TopProductDto;
import com.inventory.inventory.service.ReportService;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/daily")
    public ResponseEntity<SalesReportDto> getDailyReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportService.getDailyReport(date));
    }

    @GetMapping("/monthly")
    public ResponseEntity<SalesReportDto> getMonthlyReport(@RequestParam(required = false) String month) {
        YearMonth reportMonth = month == null || month.isBlank() ? null : YearMonth.parse(month);
        return ResponseEntity.ok(reportService.getMonthlyReport(reportMonth));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts() {
        return ResponseEntity.ok(reportService.getTopProducts());
    }
}