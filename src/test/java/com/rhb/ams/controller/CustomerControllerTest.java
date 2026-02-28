package com.rhb.ams.controller;

import com.rhb.ams.dto.*;
import com.rhb.ams.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

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

        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("John")
                .email("john@test.com")
                .build();


        CustomerResponseDTO response = CustomerResponseDTO.builder()
                .id(1L)
                .name("John")
                .email("john@test.com")
                .build();

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

        CustomerResponseDTO response = CustomerResponseDTO.builder()
                .id(1L)
                .name("John")
                .build();

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

        customer = CustomerResponseDTO.builder()
                .id(1L)
                .name("John")
                .build();

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

        CustomerRequestDTO request = CustomerRequestDTO.builder()
                .name("Updated")
                .email("update@rmail.com")
                .build();

        CustomerResponseDTO response = CustomerResponseDTO.builder()
                .id(1L)
                .name("Updated")
                .build();

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

        AccountSummaryDTO account = AccountSummaryDTO.builder()
                .id(10L)
                .accountNumber("ACC001")
                .build();

        CustomerWithAccountsDTO dto = CustomerWithAccountsDTO.builder()
                .id(1L)
                .name("John")
                .accounts(List.of(account))
                .build();

        Mockito.when(customerService.getCustomerWithAccounts(1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/v1/customers/with-accounts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accounts[0].accountNumber").value("ACC001"));
    }


    @Test
    void shouldGenerateRandomCustomers() throws Exception {

        CustomerResponseDTO customer = CustomerResponseDTO.builder()
                .id(1L)
                .name("Random")
                .build();

        Mockito.when(customerService.generateRandomCustomers(5))
                .thenReturn(List.of(customer));

        mockMvc.perform(post("/api/v1/customers/generate-random")
                        .param("count", "5"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].name").value("Random"));
    }
}