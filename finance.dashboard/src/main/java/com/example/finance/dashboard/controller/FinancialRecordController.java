package com.example.finance.dashboard.controller;

import com.example.finance.dashboard.dto.request.FinancialRecordRequest;
import com.example.finance.dashboard.dto.response.*;
import com.example.finance.dashboard.model.FinancialRecord;
import com.example.finance.dashboard.model.User;
import com.example.finance.dashboard.repository.UserRepository;
import com.example.finance.dashboard.service.FinancialRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordService recordService;
    private final UserRepository userRepository;

    // 🔥 COMMON METHOD
    private User getLoggedInUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    // ✅ CREATE RECORD
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @PostMapping
    public ResponseEntity<FinancialRecordResponse> createRecord(
            @Valid @RequestBody FinancialRecordRequest request,
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        FinancialRecord record = FinancialRecord.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .date(request.getDate())
                .description(request.getDescription())
                .build();

        FinancialRecord saved = recordService.createRecord(record, user);

        return ResponseEntity.ok(mapToResponse(saved));
    }

    // ✅ GET USER RECORDS
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping
    public ResponseEntity<List<FinancialRecordResponse>> getRecords(Authentication authentication) {

        User user = getLoggedInUser(authentication);

        List<FinancialRecordResponse> response = recordService
                .getUserRecords(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    // ✅ DELETE (Soft Delete)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {

        recordService.deleteRecord(id);
        return ResponseEntity.ok("Record deleted successfully");
    }

    // ✅ DASHBOARD SUMMARY
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/summary")
    public ResponseEntity<SummaryResponse> getSummary(Authentication authentication) {

        User user = getLoggedInUser(authentication);

        return ResponseEntity.ok(
                SummaryResponse.builder()
                        .totalIncome(recordService.getTotalIncome(user.getId()))
                        .totalExpense(recordService.getTotalExpense(user.getId()))
                        .netBalance(recordService.getNetBalance(user.getId()))
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/monthly-trends")
    public ResponseEntity<List<MonthlyTrendResponse>> getMonthlyTrends(Authentication authentication) {

        User user = getLoggedInUser(authentication);
        return ResponseEntity.ok(recordService.getMonthlyTrends(user.getId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/category-wise")
    public ResponseEntity<List<CategoryResponse>> getCategoryWise(Authentication authentication) {

        User user = getLoggedInUser(authentication);
        return ResponseEntity.ok(recordService.getCategoryWiseExpense(user.getId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard(Authentication authentication) {

        User user = getLoggedInUser(authentication);
        return ResponseEntity.ok(recordService.getDashboard(user.getId()));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ANALYST','VIEWER')")
    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredRecords(
            Authentication authentication,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        User user = getLoggedInUser(authentication);

        Pageable pageable = PageRequest.of(page, size);

        LocalDate start = (startDate != null) ? LocalDate.parse(startDate) : null;
        LocalDate end = (endDate != null) ? LocalDate.parse(endDate) : null;

        Page<FinancialRecordResponse> response = recordService
                .getFilteredRecords(user.getId(), start, end, pageable)
                .map(this::mapToResponse);

        return ResponseEntity.ok(response);
    }

    // 🔥 MAPPER METHOD
    private FinancialRecordResponse mapToResponse(FinancialRecord record) {
        return FinancialRecordResponse.builder()
                .id(record.getId())
                .amount(record.getAmount())
                .type(record.getType().name())
                .category(record.getCategory())
                .date(record.getDate())
                .description(record.getDescription())
                .build();
    }

}