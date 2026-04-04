package com.example.finance.dashboard.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MonthlyTrendResponse {
    private String month;
    private Double balance;
    private Double income;
    private Double expense;
}