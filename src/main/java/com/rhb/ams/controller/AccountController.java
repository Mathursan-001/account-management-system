package com.rhb.ams.controller;

import com.rhb.ams.dto.AccountRequestDTO;
import com.rhb.ams.dto.AccountResponseDTO;
import com.rhb.ams.entity.Account;
import com.rhb.ams.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;


    /**
     * Create a new account
     *
     * @param accountRequestDTO Account request data transfer object
     * @return Created account response DTO with HTTP 201
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        try {
            AccountResponseDTO createdAccount = accountService.createAccount(accountRequestDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAccount);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Get all accounts
     *
     * @return List of all account response DTOs
     */
    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        List<AccountResponseDTO> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    /**
     * Get account by ID
     *
     * @param id Account ID
     * @return Account response DTO if found, otherwise 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable String id) {
        return accountService.getAccountByAccountNumber(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Update an existing account
     *
     * @param id                  Account ID
     * @param accountRequestDTO Account request data transfer object
     * @return Updated account response DTO if found, otherwise 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> updateAccount(@PathVariable Long id, @Valid @RequestBody AccountRequestDTO accountRequestDTO) {
        try {
            return accountService.updateAccount(id, accountRequestDTO)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete an account by account number
     *
     * @param id
     * @return 204 No Content if successful, 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

}
