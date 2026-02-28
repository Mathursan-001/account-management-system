package com.rhb.ams.service;

import com.rhb.ams.dto.AccountRequestDTO;
import com.rhb.ams.dto.AccountResponseDTO;
import com.rhb.ams.entity.Account;
import com.rhb.ams.entity.Customer;
import com.rhb.ams.exception.ResourceNotFoundException;
import com.rhb.ams.repository.AccountRepository;
import com.rhb.ams.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AccountService
 * Tests account creation, retrieval, update, and deletion operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccountService Unit Tests")
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer testCustomer;
    private Account testAccount;
    private AccountRequestDTO accountRequestDTO;

    @BeforeEach
    void setUp() {
        // Setup test customer
        testCustomer = Customer.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .createdAt(LocalDateTime.now())
                .build();

        // Setup test account
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1234567890")
                .balance(new BigDecimal("1000.00"))
                .customer(testCustomer)
                .build();

        // Setup request DTO
        accountRequestDTO = AccountRequestDTO.builder()
                .customerId(1L)
                .balance(new BigDecimal("1000.00"))
                .build();
    }

    @Test
    @DisplayName("Should create account successfully")
    void testCreateAccountSuccess() {
        // Arrange
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        // Act
        AccountResponseDTO result = accountService.createAccount(accountRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals("ACC1234567890", result.getAccountNumber());
        assertEquals(new BigDecimal("1000.00"), result.getBalance());
        assertEquals(1L, result.getCustomerId());
        verify(customerRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should retrieve all accounts")
    void testGetAllAccounts() {
        // Arrange
        Account account2 = Account.builder()
                .id(2L)
                .accountNumber("ACC9876543210")
                .balance(new BigDecimal("2000.00"))
                .customer(testCustomer)
                .build();

        when(accountRepository.findAll()).thenReturn(Arrays.asList(testAccount, account2));

        // Act
        List<AccountResponseDTO> result = accountService.getAllAccounts();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should retrieve account by account number")
    void testGetAccountByAccountNumber() {
        // Arrange
        when(accountRepository.findByAccountNumber("ACC1234567890")).thenReturn(Optional.of(testAccount));

        // Act
        Optional<AccountResponseDTO> result = accountService.getAccountByAccountNumber("ACC1234567890");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ACC1234567890", result.get().getAccountNumber());
        verify(accountRepository, times(1)).findByAccountNumber("ACC1234567890");
    }

    @Test
    @DisplayName("Should update account successfully")
    void testUpdateAccountSuccess() {
        // Arrange
        AccountRequestDTO updateDTO = AccountRequestDTO.builder()
                .customerId(1L)
                .balance(new BigDecimal("5000.00"))
                .build();

        Account updatedAccount = Account.builder()
                .id(1L)
                .accountNumber("ACC1234567890")
                .balance(new BigDecimal("5000.00"))
                .customer(testCustomer)
                .build();

        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.save(any(Account.class))).thenReturn(updatedAccount);

        // Act
        Optional<AccountResponseDTO> result = accountService.updateAccount(1L, updateDTO);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(new BigDecimal("5000.00"), result.get().getBalance());
        verify(accountRepository, times(1)).findById(1L);
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("Should return empty Optional when updating non-existent account")
    void testUpdateAccountNotFound() {
        // Arrange
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Optional<AccountResponseDTO> result = accountService.updateAccount(999L, accountRequestDTO);

        // Assert
        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should delete account by ID")
    void testDeleteAccount() {
        // Act
        accountService.deleteAccount(1L);

        // Assert
        verify(accountRepository, times(1)).deleteById(1L);
    }


}
