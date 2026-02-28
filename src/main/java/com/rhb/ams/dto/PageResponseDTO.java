package com.rhb.ams.dto;

import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponseDTO<T> {

    private List<T> customers;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

}
