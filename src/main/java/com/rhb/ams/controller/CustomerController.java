package com.rhb.ams.controller;

import com.rhb.ams.dto.*;
import com.rhb.ams.service.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;


    /**
     * Create a new customer
     *
     * @param customerRequestDTO Customer request data transfer object
     * @return Created customer response DTO with HTTP 201
     */
    @PostMapping
    public ResponseEntity<CustomerResponseDTO> createCustomer(@Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        CustomerResponseDTO createdCustomer = customerService.createCustomer(customerRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }


    /**
     * Get customer by ID
     *
     * @param id Customer ID
     * @return Customer response DTO if found, otherwise 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Search customers with optional filters
     *
     * @param name     Filter by customer name (optional)
     * @param fromDate Filter customers created from this date (optional, format: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)
     * @param toDate   Filter customers created until this date (optional, format: yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)
     * @param pageable Pagination information
     * @return Page of customers matching the criteria
     */
    @GetMapping("/search")
    public ResponseEntity<PageResponseDTO<CustomerResponseDTO>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
        LocalDateTime toDateTime = toDate != null ? toDate.atTime(LocalTime.MAX) : null;

        Page<CustomerResponseDTO> pageResult = customerService.searchCustomers(name, fromDateTime, toDateTime, pageable);

        return ResponseEntity.ok(
                PageResponseDTO.<CustomerResponseDTO>builder()
                        .customers(pageResult.getContent())
                        .page(pageResult.getNumber())
                        .size(pageResult.getSize())
                        .totalElements(pageResult.getTotalElements())
                        .totalPages(pageResult.getTotalPages())
                        .build()
        );
    }

    /**
     * Update an existing customer
     *
     * @param id                 Customer ID
     * @param customerRequestDTO Customer request data transfer object
     * @return Updated customer response DTO if found, otherwise 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerRequestDTO customerRequestDTO) {
        return customerService.updateCustomer(id, customerRequestDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Get all accounts for a specific customer (Join endpoint)
     *
     * @param customerId Customer ID
     * @return List of account response DTOs for the customer
     */
    @GetMapping("/with-accounts/{customerId}")
    public ResponseEntity<CustomerWithAccountsDTO> getAccountsByCustomerId(@PathVariable Long customerId) {
        CustomerWithAccountsDTO customer = customerService.getCustomerWithAccounts(customerId);
        if (customer.getAccounts().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(customer);
    }

    /**
     * Delete a customer by ID
     *
     * @param id Customer ID
     * @return 204 No Content if successful, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Generate hundreds of random customers - util endpoint for demo purpose
     *
     * @param count Number of random customers to generate (default: 100, max: 1000)
     * @return List of created customer response DTOs with HTTP 201
     */
    @PostMapping("/generate-random")
    public ResponseEntity<List<CustomerResponseDTO>> generateRandomCustomers(
            @RequestParam(defaultValue = "100") int count) {

        // Limit to maximum 1000 customers per request
        if (count > 500) {
            count = 500;
        }

        if (count < 1) {
            count = 1;
        }

        List<CustomerResponseDTO> generatedCustomers = customerService.generateRandomCustomers(count);
        return ResponseEntity.status(HttpStatus.CREATED).body(generatedCustomers);
    }
}
