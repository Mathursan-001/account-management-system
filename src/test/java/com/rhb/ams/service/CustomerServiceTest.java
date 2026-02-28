package com.rhb.ams.service;

import com.rhb.ams.dto.CustomerRequestDTO;
import com.rhb.ams.dto.CustomerResponseDTO;
import com.rhb.ams.dto.CustomerWithAccountsDTO;
import com.rhb.ams.dto.AccountSummaryDTO;
import com.rhb.ams.entity.Customer;
import com.rhb.ams.entity.Account;
import com.rhb.ams.exception.ResourceNotFoundException;
import com.rhb.ams.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CustomerService
 * Tests customer creation, search, retrieval, update, and deletion operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer testCustomer;
    private CustomerRequestDTO customerRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .createdAt(LocalDateTime.now())
                .accounts(List.of())
                .build();

        // Setup request DTO
        customerRequestDTO = CustomerRequestDTO.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

    }

    @Test
    @DisplayName("Should create customer successfully")
    void testCreateCustomerSuccess() {
        // Arrange
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        // Act
        CustomerResponseDTO result = customerService.createCustomer(customerRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should retrieve customer by ID")
    void testGetCustomerById() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        Optional<CustomerResponseDTO> result = customerService.getCustomerById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return empty Optional when customer not found")
    void testGetCustomerByIdNotFound() {
        // Arrange
        when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<CustomerResponseDTO> result = customerService.getCustomerById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should update customer successfully")
    void testUpdateCustomerSuccess() {
        // Arrange
        CustomerRequestDTO updateDTO = CustomerRequestDTO.builder()
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        Customer updatedCustomer = Customer.builder()
                .id(1L)
                .name("Jane Doe")
                .email("jane@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerRepository.save(any(Customer.class))).thenReturn(updatedCustomer);

        // Act
        Optional<CustomerResponseDTO> result = customerService.updateCustomer(1L, updateDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Jane Doe", result.get().getName());
        verify(customerRepository, times(1)).findById(1L);
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    @DisplayName("Should delete customer by ID")
    void testDeleteCustomer() {
        // Act
        customerService.deleteCustomer(1L);

        // Assert
        verify(customerRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should search customers with name filter")
    void testSearchCustomersWithNameFilter() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> customerPage = new PageImpl<>(Collections.singletonList(testCustomer), pageable, 1);

        when(customerRepository.searchCustomers("John", null, null, pageable)).thenReturn(customerPage);

        // Act
        Page<CustomerResponseDTO> result = customerService.searchCustomers("John", null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository, times(1)).searchCustomers("John", null, null, pageable);
    }

    @Test
    @DisplayName("Should search customers with date filter")
    void testSearchCustomersWithDateFilter() {
        // Arrange
        LocalDateTime fromDate = LocalDateTime.now().minusDays(7);
        LocalDateTime toDate = LocalDateTime.now();

        Pageable pageable = PageRequest.of(0, 20);
        Page<Customer> customerPage = new PageImpl<>(Collections.singletonList(testCustomer), pageable, 1);

        when(customerRepository.searchCustomers(null, fromDate, toDate, pageable)).thenReturn(customerPage);

        // Act
        Page<CustomerResponseDTO> result = customerService.searchCustomers(null, fromDate, toDate, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(customerRepository, times(1)).searchCustomers(null, fromDate, toDate, pageable);
    }

    @Test
    @DisplayName("Should get customer with accounts")
    void testGetCustomerWithAccounts() {
        // Arrange
        Account account1 = Account.builder()
                .id(1L)
                .accountNumber("ACC1234567890")
                .balance(new BigDecimal("1000.00"))
                .customer(testCustomer)
                .build();

        Account account2 = Account.builder()
                .id(2L)
                .accountNumber("ACC9876543210")
                .balance(new BigDecimal("2000.00"))
                .customer(testCustomer)
                .build();

        testCustomer.setAccounts(Arrays.asList(account1, account2));

        when(customerRepository.findCustomerWithAccounts(1L)).thenReturn(Optional.of(testCustomer));

        // Act
        CustomerWithAccountsDTO result = customerService.getCustomerWithAccounts(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals(2, result.getAccounts().size());
        verify(customerRepository, times(1)).findCustomerWithAccounts(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when customer with accounts not found")
    void testGetCustomerWithAccountsNotFound() {
        // Arrange
        when(customerRepository.findCustomerWithAccounts(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            customerService.getCustomerWithAccounts(999L);
        });
        verify(customerRepository, times(1)).findCustomerWithAccounts(999L);
    }

    @Test
    @DisplayName("Should generate random customers")
    void testGenerateRandomCustomers() {
        // Arrange
        int count = 5;
        List<Customer> generatedCustomers = Arrays.asList(
                Customer.builder().id(1L).name("Customer 1").email("customer1@test.com").createdAt(LocalDateTime.now()).build(),
                Customer.builder().id(2L).name("Customer 2").email("customer2@test.com").createdAt(LocalDateTime.now()).build(),
                Customer.builder().id(3L).name("Customer 3").email("customer3@test.com").createdAt(LocalDateTime.now()).build(),
                Customer.builder().id(4L).name("Customer 4").email("customer4@test.com").createdAt(LocalDateTime.now()).build(),
                Customer.builder().id(5L).name("Customer 5").email("customer5@test.com").createdAt(LocalDateTime.now()).build()
        );

        when(customerRepository.saveAll(any())).thenReturn(generatedCustomers);

        // Act
        List<CustomerResponseDTO> result = customerService.generateRandomCustomers(count);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.size());
        verify(customerRepository, times(1)).saveAll(any());
    }
}
