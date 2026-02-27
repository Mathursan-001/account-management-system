package com.rhb.ams.service;

import com.rhb.ams.dto.AccountRequestDTO;
import com.rhb.ams.dto.AccountResponseDTO;
import com.rhb.ams.dto.CustomerResponseDTO;
import com.rhb.ams.entity.Account;
import com.rhb.ams.entity.Customer;
import com.rhb.ams.repository.AccountRepository;
import com.rhb.ams.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    /**
     * Create a new account
     *
     * @param accountRequestDTO Account data transfer object
     * @return Created account response DTO
     */
    public AccountResponseDTO createAccount(AccountRequestDTO accountRequestDTO) {
        Customer customer = customerRepository.findById(accountRequestDTO.getCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + accountRequestDTO.getCustomerId()));

        Account account = Account.builder()
                .accountNumber(generateAccountNumber())
                .balance(accountRequestDTO.getBalance())
                .customer(customer)
                .build();

        Account savedAccount = accountRepository.save(account);
        return convertToResponseDTO(savedAccount);
    }

    /**
     * Get all accounts
     *
     * @return List of all account response DTOs
     */
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get account by ID
     *
     * @param accountNumber Account ID
     * @return Optional containing the account response DTO if found
     */
    public Optional<AccountResponseDTO> getAccountByAccountNumber(String accountNumber) {
         return accountRepository.findByAccountNumber(accountNumber).map(this::convertToResponseDTO);
    }

    /**
     * Update an existing account
     *
     * @param id                  Account ID
     * @param accountRequestDTO Account updated data
     * @return Updated account response DTO
     */
    public Optional<AccountResponseDTO> updateAccount(Long id, AccountRequestDTO accountRequestDTO) {
        return accountRepository.findById(id).map(existingAccount -> {

            if (accountRequestDTO.getBalance() != null) {
                existingAccount.setBalance(accountRequestDTO.getBalance());
            }
            if (accountRequestDTO.getCustomerId() != null) {
                Customer customer = customerRepository.findById(accountRequestDTO.getCustomerId())
                        .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + accountRequestDTO.getCustomerId()));
                existingAccount.setCustomer(customer);
            }
            Account updatedAccount = accountRepository.save(existingAccount);
            return convertToResponseDTO(updatedAccount);
        });
    }

    /**
     * Delete an account by ID
     *
     * @param accountNumber Account ID
     */
    public void deleteAccount(String accountNumber) {
        accountRepository.deleteByAccountNumber(accountNumber);
    }

    /**
     * Find all accounts belonging to a specific customer
     *
     * @param customerId The ID of the customer
     * @return List of account response DTOs for the customer
     */
    public List<AccountResponseDTO> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convert Account entity to AccountResponseDTO
     *
     * @param account Account entity
     * @return AccountResponseDTO
     */
    private AccountResponseDTO convertToResponseDTO(Account account) {
        CustomerResponseDTO customerDTO = CustomerResponseDTO.builder()
                .id(account.getCustomer().getId())
                .name(account.getCustomer().getName())
                .email(account.getCustomer().getEmail())
                .createdAt(account.getCustomer().getCreatedAt())
                .build();

        return AccountResponseDTO.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .customer(customerDTO)
                .build();
    }

    private String generateAccountNumber() {
        long min = 10000000L;
        long max = 9999999999L;
        long randomNumber = min + (long) (Math.random() * (max - min + 1));
        return "ACC" + randomNumber;
    }

}
