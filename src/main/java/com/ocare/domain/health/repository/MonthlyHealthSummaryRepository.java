package com.ocare.domain.health.repository;

import com.ocare.domain.health.entity.MonthlyHealthSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 월별 건강 데이터 집계 Repository
 */
@Repository
public interface MonthlyHealthSummaryRepository extends JpaRepository<MonthlyHealthSummaryEntity, Long> {

    Optional<MonthlyHealthSummaryEntity> findByRecordKeyAndSummaryYearAndSummaryMonth(
            String recordKey, Integer summaryYear, Integer summaryMonth);

    List<MonthlyHealthSummaryEntity> findByRecordKeyOrderBySummaryYearAscSummaryMonthAsc(String recordKey);

    List<MonthlyHealthSummaryEntity> findByRecordKeyAndSummaryYearOrderBySummaryMonthAsc(
            String recordKey, Integer summaryYear);
}
