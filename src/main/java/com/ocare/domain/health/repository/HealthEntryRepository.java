package com.ocare.domain.health.repository;

import com.ocare.domain.health.entity.HealthEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 건강 데이터 원본 Repository
 */
@Repository
public interface HealthEntryRepository extends JpaRepository<HealthEntry, Long> {

    /**
     * recordKey, periodFrom, periodTo로 기존 데이터 조회 (중복 검사용)
     */
    Optional<HealthEntry> findByRecordKeyAndPeriodFromAndPeriodTo(
            String recordKey, LocalDateTime periodFrom, LocalDateTime periodTo);

    /**
     * recordKey로 모든 데이터 조회
     */
    List<HealthEntry> findByRecordKey(String recordKey);

    /**
     * recordKey와 기간으로 데이터 조회
     */
    List<HealthEntry> findByRecordKeyAndPeriodFromBetween(
            String recordKey, LocalDateTime startDateTime, LocalDateTime endDateTime);

    /**
     * 일별 집계를 위한 쿼리
     */
    @Query("SELECT h FROM HealthEntry h WHERE h.recordKey = :recordKey " +
            "AND FUNCTION('DATE', h.periodFrom) = FUNCTION('DATE', :date)")
    List<HealthEntry> findByRecordKeyAndDate(
            @Param("recordKey") String recordKey,
            @Param("date") LocalDateTime date);
}
