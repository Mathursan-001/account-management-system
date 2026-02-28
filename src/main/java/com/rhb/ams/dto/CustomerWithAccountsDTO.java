package com.rhb.ams.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class CustomerWithAccountsDTO {

    private Long id;
    private String name;
    private List<AccountSummaryDTO> accounts;

}
