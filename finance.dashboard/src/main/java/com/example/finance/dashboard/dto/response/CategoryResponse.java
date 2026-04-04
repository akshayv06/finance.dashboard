package com.example.finance.dashboard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private String category;
    private Double total;
}