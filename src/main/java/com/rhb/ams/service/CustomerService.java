package com.rhb.ams.service;

import com.rhb.ams.dto.CustomerRequestDTO;
import com.rhb.ams.dto.CustomerResponseDTO;
import com.rhb.ams.entity.Customer;
import com.rhb.ams.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    /**
     * Search customers by name, fromDate, and toDate with pagination
     *
     * @param name     Filter by customer name (optional)
     * @param fromDate Filter customers created from this date (optional)
     * @param toDate   Filter customers created until this date (optional)
     * @param pageable Pagination information
     * @return Page of customer response DTOs matching the criteria
     */
    public Page<CustomerResponseDTO> searchCustomers(String name, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable) {
        Page<Customer> customers = customerRepository.searchCustomers(name, fromDate, toDate, pageable);
        return customers.map(this::convertToResponseDTO);
    }

    /**
     * Create a new customer
     *
     * @param customerRequestDTO Customer data transfer object
     * @return Created customer response DTO
     */
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {
        Customer customer = Customer.builder()
                .name(customerRequestDTO.getName())
                .email(customerRequestDTO.getEmail())
                .createdAt(LocalDateTime.now())
                .build();
        Customer savedCustomer = customerRepository.save(customer);
        return convertToResponseDTO(savedCustomer);
    }

    /**
     * Get customer by ID
     *
     * @param id Customer ID
     * @return Optional containing the customer response DTO if found
     */
    public Optional<CustomerResponseDTO> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::convertToResponseDTO);
    }

    /**
     * Update an existing customer
     *
     * @param id                   Customer ID
     * @param customerRequestDTO Customer updated data
     * @return Updated customer response DTO
     */
    public Optional<CustomerResponseDTO> updateCustomer(Long id, CustomerRequestDTO customerRequestDTO) {
        return customerRepository.findById(id).map(existingCustomer -> {
            if (customerRequestDTO.getName() != null) {
                existingCustomer.setName(customerRequestDTO.getName());
            }
            if (customerRequestDTO.getEmail() != null) {
                existingCustomer.setEmail(customerRequestDTO.getEmail());
            }
            Customer updatedCustomer = customerRepository.save(existingCustomer);
            return convertToResponseDTO(updatedCustomer);
        });
    }

    /**
     * Delete a customer by ID
     *
     * @param id Customer ID
     */
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    /**
     * Convert Customer entity to CustomerResponseDTO
     *
     * @param customer Customer entity
     * @return CustomerResponseDTO
     */
    private CustomerResponseDTO convertToResponseDTO(Customer customer) {
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .createdAt(customer.getCreatedAt())
                .build();
    }
}
