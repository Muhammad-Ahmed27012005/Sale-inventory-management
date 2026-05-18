package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.SalesReportDto;
import com.inventory.inventory.dto.TopProductDto;
import com.inventory.inventory.entity.Sale;
import com.inventory.inventory.repository.SaleItemRepository;
import com.inventory.inventory.repository.SaleRepository;
import com.inventory.inventory.service.ReportService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;

    @Override
    @Transactional(readOnly = true)
    public SalesReportDto getDailyReport(LocalDate date) {
        LocalDate reportDate = date == null ? LocalDate.now() : date;
        List<Sale> sales = saleRepository.findBySaleDateBetween(reportDate.atStartOfDay(),
                reportDate.plusDays(1).atStartOfDay());
        return buildReport(reportDate.toString(), sales);
    }

    @Override
    @Transactional(readOnly = true)
    public SalesReportDto getMonthlyReport(YearMonth month) {
        YearMonth reportMonth = month == null ? YearMonth.now() : month;
        List<Sale> sales = saleRepository.findBySaleDateBetween(
                reportMonth.atDay(1).atStartOfDay(),
                reportMonth.plusMonths(1).atDay(1).atStartOfDay());
        return buildReport(reportMonth.toString(), sales);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopProductDto> getTopProducts() {
        return saleItemRepository.findTopSellingProducts(PageRequest.of(0, 10))
                .stream()
                .map(row -> TopProductDto.builder()
                        .productId((Long) row[0])
                        .productName((String) row[1])
                        .quantitySold(((Number) row[2]).longValue())
                        .revenue((BigDecimal) row[3])
                        .build())
                .toList();
    }

    private SalesReportDto buildReport(String period, List<Sale> sales) {
        BigDecimal revenue = sales.stream()
                .map(Sale::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return SalesReportDto.builder()
                .period(period)
                .totalSales(sales.size())
                .totalRevenue(revenue)
                .build();
    }
}