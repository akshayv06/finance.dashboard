package com.example.finance.dashboard.service;

import com.example.finance.dashboard.dto.response.CategoryResponse;
import com.example.finance.dashboard.dto.response.DashboardResponse;
import com.example.finance.dashboard.dto.response.MonthlyTrendResponse;
import com.example.finance.dashboard.dto.response.SummaryResponse;
import com.example.finance.dashboard.model.FinancialRecord;
import com.example.finance.dashboard.model.RecordType;
import com.example.finance.dashboard.model.User;
import com.example.finance.dashboard.repository.FinancialRecordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
@Slf4j
public class FinancialRecordService {

    private final FinancialRecordRepository recordRepository;

    // ================= CREATE =================
    public FinancialRecord createRecord(FinancialRecord record, User user) {
        record.setUser(user);
        return recordRepository.save(record);
    }

    // ================= READ =================
    public List<FinancialRecord> getUserRecords(Long userId) {
        return recordRepository.findByUserIdAndIsDeletedFalse(userId);
    }

    // ================= DELETE =================
    public void deleteRecord(Long id) {
        FinancialRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setDeleted(true);
        recordRepository.save(record);
    }

    // ================= SUMMARY =================
    public Double getTotalIncome(Long userId) {
        return safeDouble(recordRepository.sumByType(RecordType.INCOME, userId));
    }

    public Double getTotalExpense(Long userId) {
        return safeDouble(recordRepository.sumByType(RecordType.EXPENSE, userId));
    }

    public Double getNetBalance(Long userId) {
        return getTotalIncome(userId) - getTotalExpense(userId);
    }

    // ================= MONTHLY TRENDS =================
    @Transactional(readOnly = true)
    public List<MonthlyTrendResponse> getMonthlyTrends(Long userId) {

        log.info("Fetching monthly trends for userId={}", userId);

        return recordRepository.getMonthlyTrends(userId)
                .stream()
                .map(obj -> {
                    double income  = safeDouble(obj[1]);
                    double expense = safeDouble(obj[2]);
                    return MonthlyTrendResponse.builder()
                            .month(String.valueOf(obj[0]))    // already "YYYY-MM" from SQL
                            .income(income)
                            .expense(expense)
                            .balance(income - expense)        // computed per row, no extra DB call
                            .build();
                }).toList();
    }

    // ================= CATEGORY =================
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategoryWiseExpense(Long userId) {

        log.info("Fetching category-wise expense for userId={}", userId);

        return recordRepository.getCategoryWiseExpense(userId)
                .stream()
                .map(obj -> CategoryResponse.builder()
                        .category((String) obj[0])
                        .total(safeDouble(obj[1]))
                        .build())
                .toList();
    }

    // ================= OPTIMIZED DASHBOARD =================
    @Cacheable(value = "dashboard", key = "#userId")
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Long userId) {
        log.info("Fetching FULL dashboard for userId={}", userId);

        // Get the raw query result
        List<Object[]> summaryData = recordRepository.getDashboardSummary(userId);

        // Convert Object[] to SummaryResponse
        SummaryResponse summary = convertToSummaryResponse(summaryData);

        List<MonthlyTrendResponse> trends = getMonthlyTrends(userId);
        List<CategoryResponse> categories = getCategoryWiseExpense(userId);

        return DashboardResponse.builder()
                .summary(summary)
                .monthlyTrends(trends)
                .categoryBreakdown(categories)
                .build();
    }

    private SummaryResponse convertToSummaryResponse(List<Object[]> data) {
        if (data == null || data.isEmpty()) {
            return SummaryResponse.builder()
                    .totalIncome(0.0)
                    .totalExpense(0.0)
                    .netBalance(0.0)
                    .build();
        }

        Object[] firstRow = data.get(0);
        Double totalIncome = firstRow[0] != null ? (Double) firstRow[0] : 0.0;
        Double totalExpense = firstRow[1] != null ? (Double) firstRow[1] : 0.0;
        Double netBalance = totalIncome - totalExpense; // compute instead of reading index 2

        return SummaryResponse.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .build();
    }

    @Transactional(readOnly = true)
    public Page<FinancialRecord> getFilteredRecords(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        log.info("Fetching filtered records for userId={}, startDate={}, endDate={}",
                userId, startDate, endDate);

        // 🔥 No filter → return all paginated
        if (startDate == null || endDate == null) {
            return recordRepository.findByUserIdAndIsDeletedFalse(userId, pageable);
        }

        // 🔥 With date filter
        return recordRepository.findByUserIdAndIsDeletedFalseAndDateBetween(
                userId, startDate, endDate, pageable);
    }
    // ================= HELPER =================
    private Double safeDouble(Object value) {
        return value != null ? ((Number) value).doubleValue() : 0.0;
    }
}