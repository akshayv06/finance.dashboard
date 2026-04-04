package com.example.finance.dashboard.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DashboardResponse {

    private SummaryResponse summary;
    private List<MonthlyTrendResponse> monthlyTrends;
    private List<CategoryResponse> categoryBreakdown;
}