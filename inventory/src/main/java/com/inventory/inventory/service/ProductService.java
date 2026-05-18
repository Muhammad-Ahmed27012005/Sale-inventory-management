package com.inventory.inventory.service;

import com.inventory.inventory.dto.ProductDto;
import java.util.List;

public interface ProductService {

    List<ProductDto> getAllProducts();

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);

    List<ProductDto> searchProducts(String query);

    ProductDto undoLastDelete();
}