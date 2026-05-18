package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.DashboardStatsDto;
import com.inventory.inventory.dto.SaleItemResponse;
import com.inventory.inventory.dto.SaleResponse;
import com.inventory.inventory.entity.Sale;
import com.inventory.inventory.repository.CustomerRepository;
import com.inventory.inventory.repository.ProductRepository;
import com.inventory.inventory.repository.SaleRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements com.inventory.inventory.service.DashboardService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getStats() {
        List<String> labels = new ArrayList<>();
        List<BigDecimal> revenue = new ArrayList<>();

        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            labels.add(date.getMonthValue() + "/" + date.getDayOfMonth());
            BigDecimal dayRevenue = saleRepository
                    .findBySaleDateBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay())
                    .stream()
                    .map(Sale::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            revenue.add(dayRevenue);
        }

        return DashboardStatsDto.builder()
                .totalProducts(productRepository.count())
                .totalSales(saleRepository.count())
                .totalCustomers(customerRepository.count())
                .lowStockCount(productRepository.findByQuantityLessThanEqual(LOW_STOCK_THRESHOLD).size())
                .recentTransactions(
                        saleRepository.findTop5ByOrderBySaleDateDesc().stream().map(this::toSaleResponse).toList())
                .revenueLabels(labels)
                .revenueData(revenue)
                .build();
    }

    private SaleResponse toSaleResponse(Sale sale) {
        return SaleResponse.builder()
                .saleId(sale.getSaleId())
                .customerName(sale.getCustomerName())
                .totalAmount(sale.getTotalAmount())
                .saleDate(sale.getSaleDate())
                .items(sale.getItems().stream()
                        .map(item -> SaleItemResponse.builder()
                                .productId(item.getProduct().getProductId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .lineTotal(item.getLineTotal())
                                .build())
                        .toList())
                .build();
    }
}