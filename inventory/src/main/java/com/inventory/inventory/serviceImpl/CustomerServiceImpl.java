package com.inventory.inventory.serviceImpl;


import com.inventory.inventory.dto.CustomerDto;
import com.inventory.inventory.dto.SaleItemResponse;
import com.inventory.inventory.dto.SaleResponse;
import com.inventory.inventory.entity.Customer;
import com.inventory.inventory.entity.Sale;
import com.inventory.inventory.exception.ResourceNotFoundException;
import com.inventory.inventory.repository.CustomerRepository;
import com.inventory.inventory.repository.SaleRepository;
import com.inventory.inventory.service.CustomerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final SaleRepository saleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional
    public CustomerDto createCustomer(CustomerDto customerDto) {
        Customer customer = Customer.builder()
                .name(customerDto.getName().trim())
                .phone(customerDto.getPhone())
                .address(customerDto.getAddress())
                .build();
        return toDto(customerRepository.save(customer));
    }

    @Override
    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + id));
        customerRepository.delete(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> searchCustomers(String query) {
        String keyword = query == null ? "" : query.trim();
        if (keyword.isBlank()) {
            return getAllCustomers();
        }
        return customerRepository.findByNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SaleResponse> getPurchaseHistory(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id " + customerId));
        return saleRepository.findByCustomerNameIgnoreCaseOrderBySaleDateDesc(customer.getName())
                .stream()
                .map(this::toSaleResponse)
                .toList();
    }

    private CustomerDto toDto(Customer customer) {
        return CustomerDto.builder()
                .customerId(customer.getCustomerId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .address(customer.getAddress())
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