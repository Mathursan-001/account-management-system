package com.rhb.ams.controller;

import com.rhb.ams.dto.*;
import com.rhb.ams.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldCreateCustomer() throws Exception {

        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("John");
        request.setEmail("john@test.com");


        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(1L);
        response.setName("John");
        response.setEmail("john@test.com");

        Mockito.when(customerService.createCustomer(any()))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John"));
    }


    @Test
    void shouldReturnCustomerById() throws Exception {

        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(1L);
        response.setName("John");

        Mockito.when(customerService.getCustomerById(1L))
                .thenReturn(Optional.of(response));

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John"));
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {

        Mockito.when(customerService.getCustomerById(1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isNotFound());
    }


    @Test
    void shouldSearchCustomers() throws Exception {

        CustomerResponseDTO customer = new CustomerResponseDTO();
        customer.setId(1L);
        customer.setName("John");

        Page<CustomerResponseDTO> page =
                new PageImpl<>(List.of(customer), PageRequest.of(0, 20), 1);

        Mockito.when(customerService.searchCustomers(
                        any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/v1/customers/search")
                        .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers[0].name").value("John"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }


    @Test
    void shouldUpdateCustomer() throws Exception {

        CustomerRequestDTO request = new CustomerRequestDTO();
        request.setName("Updated");
        request.setEmail("update@rmail.com");

        CustomerResponseDTO response = new CustomerResponseDTO();
        response.setId(1L);
        response.setName("Updated");

        Mockito.when(customerService.updateCustomer(eq(1L), any()))
                .thenReturn(Optional.of(response));

        mockMvc.perform(put("/api/v1/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }


    @Test
    void shouldDeleteCustomer() throws Exception {

        Mockito.doNothing().when(customerService).deleteCustomer(1L);

        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(customerService).deleteCustomer(1L);
    }


    @Test
    void shouldReturnCustomerWithAccounts() throws Exception {

        AccountSummaryDTO account = new AccountSummaryDTO();
        account.setId(10L);
        account.setAccountNumber("ACC001");

        CustomerWithAccountsDTO dto = new CustomerWithAccountsDTO();
        dto.setId(1L);
        dto.setName("John");
        dto.setAccounts(List.of(account));

        Mockito.when(customerService.getCustomerWithAccounts(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/customers/with-accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].accountNumber").value("ACC001"));
    }


    @Test
    void shouldGenerateRandomCustomers() throws Exception {

        CustomerResponseDTO customer = new CustomerResponseDTO();
        customer.setId(1L);
        customer.setName("Random");

        Mockito.when(customerService.generateRandomCustomers(5))
                .thenReturn(List.of(customer));

        mockMvc.perform(post("/api/v1/customers/generate-random")
                        .param("count", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("Random"));
    }
}