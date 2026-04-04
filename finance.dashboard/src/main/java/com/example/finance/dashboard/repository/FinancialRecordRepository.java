package com.example.finance.dashboard.repository;


import com.example.finance.dashboard.model.FinancialRecord;
import com.example.finance.dashboard.model.RecordType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    // ✅ Correct (filters soft deleted records)
    List<FinancialRecord> findByUserIdAndIsDeletedFalse(Long userId);

    Page<FinancialRecord> findByUserIdAndIsDeletedFalse(Long userId, Pageable pageable);

    Page<FinancialRecord> findByUserIdAndIsDeletedFalseAndDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable);

    @Query("""
    SELECT COALESCE(SUM(r.amount), 0) 
    FROM FinancialRecord r 
    WHERE r.type = :type 
    AND r.user.id = :userId 
    AND r.isDeleted = false
""")
    Double sumByType(@Param("type") RecordType type,
                     @Param("userId") Long userId);

    // 📊 Monthly Trends
    @Query("""
    SELECT 
        FUNCTION('MONTH', r.date),
        SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END),
        SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END)
    FROM FinancialRecord r
    WHERE r.user.id = :userId AND r.isDeleted = false
    GROUP BY FUNCTION('MONTH', r.date)
    ORDER BY FUNCTION('MONTH', r.date)
""")
    List<Object[]> getMonthlyTrends(@Param("userId") Long userId);


    // 📊 Category-wise
    @Query("""
    SELECT r.category, SUM(r.amount)
    FROM FinancialRecord r
    WHERE r.user.id = :userId 
    AND r.type = 'EXPENSE'
    AND r.isDeleted = false
    GROUP BY r.category
""")
    List<Object[]> getCategoryWiseExpense(@Param("userId") Long userId);


    // 🚀 Optimized Dashboard
    @Query("""
    SELECT 
        COALESCE(SUM(CASE WHEN r.type = 'INCOME' THEN r.amount ELSE 0 END), 0),
        COALESCE(SUM(CASE WHEN r.type = 'EXPENSE' THEN r.amount ELSE 0 END), 0)
    FROM FinancialRecord r
    WHERE r.user.id = :userId AND r.isDeleted = false
""")
    List<Object[]> getDashboardSummary(@Param("userId") Long userId);
}