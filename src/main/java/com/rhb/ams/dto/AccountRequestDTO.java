package com.rhb.ams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDTO {

    @NotNull(message = "Balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be greater than or equal to 0")
    private BigDecimal balance;

    @NotNull(message = "Customer ID is required")
    private Long customerId;
}
