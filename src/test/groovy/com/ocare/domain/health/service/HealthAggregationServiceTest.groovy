package com.ocare.domain.health.service

import com.ocare.domain.health.entity.DailyHealthSummaryEntity
import com.ocare.domain.health.entity.HealthEntryEntity
import com.ocare.domain.health.entity.MonthlyHealthSummaryEntity
import com.ocare.domain.health.repository.DailyHealthSummaryRepository
import com.ocare.domain.health.repository.HealthEntryRepository
import com.ocare.domain.health.repository.MonthlyHealthSummaryRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime

class HealthAggregationServiceTest extends Specification {

    HealthEntryRepository healthEntryRepository = Mock()
    DailyHealthSummaryRepository dailySummaryRepository = Mock()
    MonthlyHealthSummaryRepository monthlySummaryRepository = Mock()

    @Subject
    HealthAggregationService healthAggregationService = new HealthAggregationService(
            healthEntryRepository, dailySummaryRepository, monthlySummaryRepository)

    def "집계 업데이트 성공 테스트 - 새 데이터 생성"() {
        given:
        String recordKey = "test-record-key"

        List<HealthEntryEntity> entries = [
                createHealthEntry(1L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 0), 1000, 50.0f, 0.5f),
                createHealthEntry(2L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 10), 1500, 75.0f, 0.7f)
        ]

        when:
        healthAggregationService.updateAggregations(recordKey)

        then:
        1 * healthEntryRepository.findByRecordKey(recordKey) >> entries
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, LocalDate.of(2024, 1, 1)) >> Optional.empty()
        1 * dailySummaryRepository.save(_ as DailyHealthSummaryEntity)
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, 2024, 1) >> Optional.empty()
        1 * monthlySummaryRepository.save(_ as MonthlyHealthSummaryEntity)
    }

    def "집계 업데이트 성공 테스트 - 기존 데이터 갱신"() {
        given:
        String recordKey = "test-record-key"

        List<HealthEntryEntity> entries = [
                createHealthEntry(1L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 0), 1000, 50.0f, 0.5f)
        ]

        DailyHealthSummaryEntity existingDaily = DailyHealthSummaryEntity.builder()
                .id(1L)
                .recordKey(recordKey)
                .summaryDate(LocalDate.of(2024, 1, 1))
                .totalSteps(500)
                .totalCalories(25.0f)
                .totalDistance(0.2f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()

        MonthlyHealthSummaryEntity existingMonthly = MonthlyHealthSummaryEntity.builder()
                .id(1L)
                .recordKey(recordKey)
                .summaryYear(2024)
                .summaryMonth(1)
                .totalSteps(500)
                .totalCalories(25.0f)
                .totalDistance(0.2f)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()

        when:
        healthAggregationService.updateAggregations(recordKey)

        then:
        1 * healthEntryRepository.findByRecordKey(recordKey) >> entries
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, LocalDate.of(2024, 1, 1)) >> Optional.of(existingDaily)
        0 * dailySummaryRepository.save(_ as DailyHealthSummaryEntity)
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, 2024, 1) >> Optional.of(existingMonthly)
        0 * monthlySummaryRepository.save(_ as MonthlyHealthSummaryEntity)

        existingDaily.totalSteps == 1000
        existingMonthly.totalSteps == 1000
    }

    def "집계 업데이트 테스트 - 엔트리 없음"() {
        given:
        String recordKey = "empty-record-key"

        when:
        healthAggregationService.updateAggregations(recordKey)

        then:
        1 * healthEntryRepository.findByRecordKey(recordKey) >> []
        0 * dailySummaryRepository.findByRecordKeyAndSummaryDate(_, _)
        0 * dailySummaryRepository.save(_)
        0 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(_, _, _)
        0 * monthlySummaryRepository.save(_)
    }

    def "집계 업데이트 테스트 - 여러 날짜 데이터"() {
        given:
        String recordKey = "test-record-key"

        List<HealthEntryEntity> entries = [
                createHealthEntry(1L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 0), 1000, 50.0f, 0.5f),
                createHealthEntry(2L, recordKey, LocalDateTime.of(2024, 1, 2, 10, 0), 2000, 100.0f, 1.0f),
                createHealthEntry(3L, recordKey, LocalDateTime.of(2024, 2, 1, 10, 0), 3000, 150.0f, 1.5f)
        ]

        when:
        healthAggregationService.updateAggregations(recordKey)

        then:
        1 * healthEntryRepository.findByRecordKey(recordKey) >> entries

        // 일별 집계: 3개 날짜
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, LocalDate.of(2024, 1, 1)) >> Optional.empty()
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, LocalDate.of(2024, 1, 2)) >> Optional.empty()
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, LocalDate.of(2024, 2, 1)) >> Optional.empty()
        3 * dailySummaryRepository.save(_ as DailyHealthSummaryEntity)

        // 월별 집계: 2개 월
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, 2024, 1) >> Optional.empty()
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, 2024, 2) >> Optional.empty()
        2 * monthlySummaryRepository.save(_ as MonthlyHealthSummaryEntity)
    }

    def "집계 업데이트 테스트 - 같은 날 여러 엔트리 합산"() {
        given:
        String recordKey = "test-record-key"

        List<HealthEntryEntity> entries = [
                createHealthEntry(1L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 0), 1000, 50.0f, 0.5f),
                createHealthEntry(2L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 10), 1500, 75.0f, 0.7f),
                createHealthEntry(3L, recordKey, LocalDateTime.of(2024, 1, 1, 10, 20), 2000, 100.0f, 1.0f)
        ]

        when:
        healthAggregationService.updateAggregations(recordKey)

        then:
        1 * healthEntryRepository.findByRecordKey(recordKey) >> entries
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, LocalDate.of(2024, 1, 1)) >> Optional.empty()
        1 * dailySummaryRepository.save({ DailyHealthSummaryEntity entity ->
            entity.totalSteps == 4500 &&
            entity.totalCalories == 225.0f &&
            entity.totalDistance == 2.2f
        })
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, 2024, 1) >> Optional.empty()
        1 * monthlySummaryRepository.save({ MonthlyHealthSummaryEntity entity ->
            entity.totalSteps == 4500 &&
            entity.totalCalories == 225.0f &&
            entity.totalDistance == 2.2f
        })
    }

    // Helper method
    private HealthEntryEntity createHealthEntry(Long id, String recordKey, LocalDateTime periodFrom,
                                                 Integer steps, Float calories, Float distance) {
        return HealthEntryEntity.builder()
                .id(id)
                .recordKey(recordKey)
                .periodFrom(periodFrom)
                .periodTo(periodFrom.plusMinutes(10))
                .steps(steps)
                .calories(calories)
                .distance(distance)
                .createdAt(LocalDateTime.now())
                .build()
    }
}
