package com.ocare.domain.health.repository;

import com.ocare.domain.health.entity.MonthlyHealthSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 월별 건강 데이터 집계 Repository
 */
@Repository
public interface MonthlyHealthSummaryRepository extends JpaRepository<MonthlyHealthSummary, Long> {

    /**
     * recordKey, 연도, 월로 월별 집계 조회
     */
    Optional<MonthlyHealthSummary> findByRecordKeyAndSummaryYearAndSummaryMonth(
            String recordKey, Integer summaryYear, Integer summaryMonth);

    /**
     * recordKey로 모든 월별 집계 조회
     */
    List<MonthlyHealthSummary> findByRecordKeyOrderBySummaryYearAscSummaryMonthAsc(String recordKey);

    /**
     * recordKey와 연도로 월별 집계 조회
     */
    List<MonthlyHealthSummary> findByRecordKeyAndSummaryYearOrderBySummaryMonthAsc(
            String recordKey, Integer summaryYear);
}
