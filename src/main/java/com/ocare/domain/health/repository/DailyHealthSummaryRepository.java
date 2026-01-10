package com.ocare.domain.health.repository;

import com.ocare.domain.health.entity.DailyHealthSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 일별 건강 데이터 집계 Repository
 */
@Repository
public interface DailyHealthSummaryRepository extends JpaRepository<DailyHealthSummary, Long> {

    /**
     * recordKey와 날짜로 일별 집계 조회
     */
    Optional<DailyHealthSummary> findByRecordKeyAndSummaryDate(String recordKey, LocalDate summaryDate);

    /**
     * recordKey로 모든 일별 집계 조회
     */
    List<DailyHealthSummary> findByRecordKeyOrderBySummaryDateAsc(String recordKey);

    /**
     * recordKey와 기간으로 일별 집계 조회
     */
    List<DailyHealthSummary> findByRecordKeyAndSummaryDateBetweenOrderBySummaryDateAsc(
            String recordKey, LocalDate startDate, LocalDate endDate);
}
