package com.rhb.ams.service;

import com.rhb.ams.dto.CustomerRequestDTO;
import com.rhb.ams.dto.CustomerResponseDTO;
import com.rhb.ams.dto.ExternalResponseDTO;
import com.rhb.ams.entity.Customer;
import com.rhb.ams.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.time.temporal.TemporalUnit;
import java.util.*;

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
     * @param id                 Customer ID
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
     * Generate random customers in bulk
     *
     * @param count Number of customers to generate
     * @return List of created customer response DTOs
     */
    public List<CustomerResponseDTO> generateRandomCustomers(int count) {
        List<Customer> customersToSave = new ArrayList<>();
        Random random = new Random();
        String[] firstNames = {"John", "Jane", "Michael", "Emily", "David", "Sarah", "Robert", "Jessica", "James", "Laura",
                "William", "Mary", "Richard", "Patricia", "Charles", "Jennifer", "Daniel", "Linda", "Matthew", "Barbara"};
        String[] lastNames = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
                "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin"};
        String[] domains = {"gmail.com", "yahoo.com", "outlook.com", "company.com", "mail.com", "example.com", "test.com", "domain.com"};

        LocalDateTime[] randomDates = new LocalDateTime[5];

        for (int i = 0; i < 5; i++) {
            LocalDateTime date = LocalDateTime.now().minusDays(i);
            randomDates[i] = date;
        }

        for (int i = 0; i < count; i++) {
            String firstName = firstNames[random.nextInt(firstNames.length)];
            String lastName = lastNames[random.nextInt(lastNames.length)];
            String name = firstName + " " + lastName;
            String email = (firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@" + domains[random.nextInt(domains.length)]);
            LocalDateTime createdAt = randomDates[random.nextInt(randomDates.length)];

            Customer customer = Customer.builder()
                    .name(name)
                    .email(email)
                    .createdAt(createdAt)
                    .build();

            customersToSave.add(customer);
        }

        List<Customer> savedCustomers = customerRepository.saveAll(customersToSave);
        List<CustomerResponseDTO> responseDTOs = new ArrayList<>();
        for (Customer customer : savedCustomers) {
            responseDTOs.add(convertToResponseDTO(customer));
        }
        return responseDTOs;
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
