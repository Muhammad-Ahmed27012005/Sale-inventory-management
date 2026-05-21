package com.inventory.inventory.util;

import com.inventory.inventory.dto.SaleItemRequest;
import com.inventory.inventory.entity.Product;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class DsaUtil {

    public List<Product> mergeSortProductsByName(List<Product> products) {
        if (products.size() <= 1) {
            return new ArrayList<>(products);
        }
        int middle = products.size() / 2;
        List<Product> left = mergeSortProductsByName(products.subList(0, middle));
        List<Product> right = mergeSortProductsByName(products.subList(middle, products.size()));
        return merge(left, right);
    }

    public int binarySearchProductByName(List<Product> sortedProducts, String query) {
        int low = 0;
        int high = sortedProducts.size() - 1;
        String target = query == null ? "" : query.trim().toLowerCase();

        while (low <= high) {
            int mid = low + (high - low) / 2;
            String value = sortedProducts.get(mid).getName().toLowerCase();
            int comparison = value.compareTo(target);
            if (comparison == 0)
                return mid;
            if (comparison < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return -1;
    }

    public Map<Long, Integer> inventoryHashMap(List<Product> products) {
        Map<Long, Integer> inventory = new HashMap<>();
        for (Product product : products) {
            inventory.put(product.getProductId(), product.getQuantity());
        }
        return inventory;
    }

    public Queue<SaleItemRequest> billingQueue(List<SaleItemRequest> requests) {
        return new ArrayDeque<>(requests);
    }

    private List<Product> merge(List<Product> left, List<Product> right) {
        List<Product> sorted = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            String leftName = left.get(i).getName().toLowerCase();
            String rightName = right.get(j).getName().toLowerCase();
            if (leftName.compareTo(rightName) <= 0) {
                sorted.add(left.get(i++));
            } else {
                sorted.add(right.get(j++));
            }
        }
        while (i < left.size())
            sorted.add(left.get(i++));
        while (j < right.size())
            sorted.add(right.get(j++));
        return sorted;
    }
}