package com.example.finance.dashboard.dto.response;

import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialRecordResponse {

    private Long id;

    private Double amount;

    private String type;        // INCOME / EXPENSE

    private String category;

    private LocalDate date;

    private String description;
}