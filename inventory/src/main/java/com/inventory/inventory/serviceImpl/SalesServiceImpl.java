package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.SaleItemRequest;
import com.inventory.inventory.dto.SaleItemResponse;
import com.inventory.inventory.dto.SaleRequest;
import com.inventory.inventory.dto.SaleResponse;
import com.inventory.inventory.entity.Product;
import com.inventory.inventory.entity.Sale;
import com.inventory.inventory.entity.SaleItem;
import com.inventory.inventory.exception.BadRequestException;
import com.inventory.inventory.exception.ResourceNotFoundException;
import com.inventory.inventory.repository.ProductRepository;
import com.inventory.inventory.repository.SaleRepository;
import com.inventory.inventory.service.SalesService;
import com.inventory.inventory.util.DsaUtil;
import java.math.BigDecimal;
import java.util.List;
import java.util.Queue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalesServiceImpl implements SalesService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final DsaUtil dsaUtil;

    @Override
    @Transactional
    public SaleResponse createSale(SaleRequest saleRequest) {
        Queue<SaleItemRequest> billingQueue = dsaUtil.billingQueue(saleRequest.getItems());
        Sale sale = new Sale();
        sale.setCustomerName(saleRequest.getCustomerName().trim());

        BigDecimal total = BigDecimal.ZERO;
        while (!billingQueue.isEmpty()) {
            SaleItemRequest itemRequest = billingQueue.poll();
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Product not found with id " + itemRequest.getProductId()));

            if (product.getQuantity() < itemRequest.getQuantity()) {
                throw new BadRequestException("Not enough stock for " + product.getName());
            }

            product.setQuantity(product.getQuantity() - itemRequest.getQuantity());
            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(product.getPrice())
                    .lineTotal(lineTotal)
                    .build();

            sale.getItems().add(saleItem);
            total = total.add(lineTotal);
        }

        sale.setTotalAmount(total);
        return toResponse(saleRepository.save(sale));
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getAllSales() {
        return saleRepository.findAllByOrderBySaleDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SaleResponse getSaleById(Long id) {
        return saleRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id " + id));
    }

    private SaleResponse toResponse(Sale sale) {
        List<SaleItemResponse> items = sale.getItems()
                .stream()
                .map(item -> SaleItemResponse.builder()
                        .productId(item.getProduct().getProductId())
                        .productName(item.getProduct().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .lineTotal(item.getLineTotal())
                        .build())
                .toList();

        return SaleResponse.builder()
                .saleId(sale.getSaleId())
                .customerName(sale.getCustomerName())
                .totalAmount(sale.getTotalAmount())
                .saleDate(sale.getSaleDate())
                .items(items)
                .build();
    }
}