package com.ocare.domain.health.repository;

import com.ocare.domain.health.entity.HealthEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 건강 데이터 원본 Repository
 */
@Repository
public interface HealthEntryRepository extends JpaRepository<HealthEntryEntity, Long> {

    Optional<HealthEntryEntity> findByRecordKeyAndPeriodFromAndPeriodTo(
            String recordKey, LocalDateTime periodFrom, LocalDateTime periodTo);

    List<HealthEntryEntity> findByRecordKey(String recordKey);

    List<HealthEntryEntity> findByRecordKeyAndPeriodFromBetween(
            String recordKey, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
