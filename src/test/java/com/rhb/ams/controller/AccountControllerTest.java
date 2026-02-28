package com.rhb.ams.controller;


import com.rhb.ams.dto.AccountRequestDTO;
import com.rhb.ams.dto.AccountResponseDTO;
import com.rhb.ams.service.AccountService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateAccount() throws Exception {

        AccountRequestDTO requestDTO = new AccountRequestDTO();
        requestDTO.setBalance(BigDecimal.valueOf(1000));
        requestDTO.setCustomerId(1L);

        AccountResponseDTO responseDTO = new AccountResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setAccountNumber("ACC123");
        responseDTO.setBalance(BigDecimal.valueOf(1000));
        responseDTO.setCustomerId(1L);

        Mockito.when(accountService.createAccount(any(AccountRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void shouldReturnAllAccounts() throws Exception {

        AccountResponseDTO account = new AccountResponseDTO();
        account.setId(1L);
        account.setAccountNumber("ACC123");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setCustomerId(1L);

        Mockito.when(accountService.getAllAccounts())
                .thenReturn(List.of(account));

        mockMvc.perform(get("/api/v1/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value("ACC123"));
    }

    @Test
    void shouldReturnAccountByAccountNumber() throws Exception {

        AccountResponseDTO account = new AccountResponseDTO();
        account.setId(1L);
        account.setAccountNumber("ACC123");
        account.setBalance(BigDecimal.valueOf(1000));
        account.setCustomerId(1L);

        Mockito.when(accountService.getAccountByAccountNumber("ACC123"))
                .thenReturn(Optional.of(account));

        mockMvc.perform(get("/api/v1/accounts/ACC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC123"));
    }

    @Test
    void shouldReturn404IfAccountNotFound() throws Exception {

        Mockito.when(accountService.getAccountByAccountNumber("UNKNOWN"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/accounts/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldDeleteAccount() throws Exception {

        Mockito.doNothing().when(accountService).deleteAccount(1L);

        mockMvc.perform(delete("/api/v1/accounts/1"))
                .andExpect(status().isNoContent());
    }
}