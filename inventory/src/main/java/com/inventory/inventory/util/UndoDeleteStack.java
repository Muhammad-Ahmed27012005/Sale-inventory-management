package com.inventory.inventory.util;

import com.inventory.inventory.entity.Product;
import org.springframework.stereotype.Component;
import java.util.Stack;

@Component
public class UndoDeleteStack {
    private final Stack<Product> deletedProducts = new Stack<>();

    public synchronized void push(Product product) {
        Product snapshot = Product.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
        deletedProducts.push(snapshot);
    }

    public synchronized Product pop() {
        return deletedProducts.isEmpty() ? null : deletedProducts.pop();
    }

    public synchronized boolean hasItems() {
        return !deletedProducts.isEmpty();
    }

    public synchronized void clear() {
        deletedProducts.clear();
    }
}