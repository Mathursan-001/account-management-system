package com.rhb.ams.controller;

import com.rhb.ams.dto.CustomerRequestDTO;
import com.rhb.ams.dto.CustomerResponseDTO;
import com.rhb.ams.service.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


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
    public ResponseEntity<Page<CustomerResponseDTO>> searchCustomers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @PageableDefault(size = 20, page = 0, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        LocalDateTime fromDateTime = null;
        LocalDateTime toDateTime = null;

        if (fromDate != null && !fromDate.isEmpty()) {
            fromDateTime = LocalDateTime.parse(fromDate, formatter);
        }
        if (toDate != null && !toDate.isEmpty()) {
            toDateTime = LocalDateTime.parse(toDate, formatter);
        }

        Page<CustomerResponseDTO> customers = customerService.searchCustomers(name, fromDateTime, toDateTime, pageable);
        return ResponseEntity.ok(customers);
    }

    /**
     * Update an existing customer
     *
     * @param id                   Customer ID
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
     * Delete a customer by ID
     *
     * @param id Customer ID
     * @return 204 No Content if successful, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        if (customerService.getCustomerById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
