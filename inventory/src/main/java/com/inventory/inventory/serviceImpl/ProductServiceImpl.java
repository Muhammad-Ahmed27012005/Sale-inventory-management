package com.inventory.inventory.serviceImpl;

import com.inventory.inventory.dto.ProductDto;
import com.inventory.inventory.entity.Product;
import com.inventory.inventory.exception.BadRequestException;
import com.inventory.inventory.exception.ResourceNotFoundException;
import com.inventory.inventory.repository.ProductRepository;
import com.inventory.inventory.service.ProductService;
import com.inventory.inventory.util.DsaUtil;
import com.inventory.inventory.util.UndoDeleteStack;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final DsaUtil dsaUtil;
    private final UndoDeleteStack undoDeleteStack;

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return dsaUtil.mergeSortProductsByName(productRepository.findAll())
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductDto createProduct(ProductDto productDto) {
        Product product = Product.builder()
                .name(productDto.getName().trim())
                .category(productDto.getCategory().trim())
                .price(productDto.getPrice())
                .quantity(productDto.getQuantity())
                .build();
        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public ProductDto updateProduct(Long id, ProductDto productDto) {
        Product product = findProduct(id);
        if (productDto.getName() != null && !productDto.getName().isBlank()) {
            product.setName(productDto.getName().trim());
        }
        if (productDto.getCategory() != null && !productDto.getCategory().isBlank()) {
            product.setCategory(productDto.getCategory().trim());
        }
        if (productDto.getPrice() != null && productDto.getPrice().compareTo(java.math.BigDecimal.ZERO) > 0) {
            product.setPrice(productDto.getPrice());
        }
        if (productDto.getQuantity() != null && productDto.getQuantity() >= 0) {
            product.setQuantity(productDto.getQuantity());
        }
        return toDto(productRepository.save(product));
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        Product product = findProduct(id);
        productRepository.delete(product);
        productRepository.flush();
        undoDeleteStack.push(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDto> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllProducts();
        }
        String normalized = query.trim().toLowerCase();
        List<Product> sorted = dsaUtil.mergeSortProductsByName(productRepository.findAll());
        Map<Long, Product> results = new LinkedHashMap<>();

        int exactMatchIndex = dsaUtil.binarySearchProductByName(sorted, normalized);
        if (exactMatchIndex >= 0) {
            Product exact = sorted.get(exactMatchIndex);
            results.put(exact.getProductId(), exact);
        }

        sorted.stream()
                .filter(product -> product.getName().toLowerCase().contains(normalized)
                        || product.getCategory().toLowerCase().contains(normalized))
                .forEach(product -> results.putIfAbsent(product.getProductId(), product));

        return results.values().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional
    public ProductDto undoLastDelete() {
        Product product = undoDeleteStack.pop();
        if (product == null) {
            throw new BadRequestException("No deleted product is available to restore");
        }
        // Check if product still exists (might have been recreated)
        if (productRepository.findById(product.getProductId()).isPresent()) {
            throw new BadRequestException("Product already exists. Cannot restore.");
        }
        product.setProductId(null); // Let DB generate new ID if needed, or keep if using IDENTITY
        return toDto(productRepository.save(product));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id " + id));
    }

    private ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}