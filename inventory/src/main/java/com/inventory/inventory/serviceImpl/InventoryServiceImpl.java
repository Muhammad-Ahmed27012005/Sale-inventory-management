package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.InventoryItemDto;
import com.inventory.inventory.entity.Product;
import com.inventory.inventory.exception.ResourceNotFoundException;
import com.inventory.inventory.repository.ProductRepository;
import com.inventory.inventory.service.InventoryService;
import com.inventory.inventory.util.DsaUtil;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final ProductRepository productRepository;
    private final DsaUtil dsaUtil;

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemDto> getInventory() {
        List<Product> products = dsaUtil.mergeSortProductsByName(productRepository.findAll());
        Map<Long, Integer> inventoryStorage = dsaUtil.inventoryHashMap(products);
        return products.stream()
                .map(product -> toInventoryDto(product, inventoryStorage.get(product.getProductId())))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItemDto> getLowStock() {
        return productRepository.findByQuantityLessThanEqual(LOW_STOCK_THRESHOLD)
                .stream()
                .map(product -> toInventoryDto(product, product.getQuantity()))
                .toList();
    }

    @Override
    @Transactional
    public InventoryItemDto updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + productId));
        product.setQuantity(quantity);
        return toInventoryDto(productRepository.save(product), quantity);
    }

    private InventoryItemDto toInventoryDto(Product product, Integer quantity) {
        return InventoryItemDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .quantity(quantity)
                .status(status(quantity))
                .build();
    }

    private String status(Integer quantity) {
        if (quantity == null || quantity == 0) {
            return "OUT_OF_STOCK";
        }
        if (quantity <= LOW_STOCK_THRESHOLD) {
            return "LOW_STOCK";
        }
        return "IN_STOCK";
    }
}