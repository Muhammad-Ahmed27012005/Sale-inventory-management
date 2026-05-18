package com.inventory.inventory.service;

import com.inventory.inventory.dto.SalesReportDto;
import com.inventory.inventory.dto.TopProductDto;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ReportService {

    SalesReportDto getDailyReport(LocalDate date);

    SalesReportDto getMonthlyReport(YearMonth month);

    List<TopProductDto> getTopProducts();
}