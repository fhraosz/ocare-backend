package com.ocare.domain.health.repository;

import com.ocare.domain.health.entity.DailyHealthSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 일별 건강 데이터 집계 Repository
 */
@Repository
public interface DailyHealthSummaryRepository extends JpaRepository<DailyHealthSummaryEntity, Long> {

    Optional<DailyHealthSummaryEntity> findByRecordKeyAndSummaryDate(String recordKey, LocalDate summaryDate);

    List<DailyHealthSummaryEntity> findByRecordKeyOrderBySummaryDateAsc(String recordKey);

    List<DailyHealthSummaryEntity> findByRecordKeyAndSummaryDateBetweenOrderBySummaryDateAsc(
            String recordKey, LocalDate startDate, LocalDate endDate);
}
