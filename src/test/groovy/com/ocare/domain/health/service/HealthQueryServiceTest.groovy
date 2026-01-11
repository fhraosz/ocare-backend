package com.ocare.domain.health.service

import com.ocare.domain.health.dto.DailySummaryResponse
import com.ocare.domain.health.dto.MonthlySummaryResponse
import com.ocare.domain.health.entity.DailyHealthSummaryEntity
import com.ocare.domain.health.entity.MonthlyHealthSummaryEntity
import com.ocare.domain.health.repository.DailyHealthSummaryRepository
import com.ocare.domain.health.repository.MonthlyHealthSummaryRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.LocalDateTime

class HealthQueryServiceTest extends Specification {

    DailyHealthSummaryRepository dailySummaryRepository = Mock()
    MonthlyHealthSummaryRepository monthlySummaryRepository = Mock()

    @Subject
    HealthQueryService healthQueryService = new HealthQueryService(dailySummaryRepository, monthlySummaryRepository)

    def "일별 집계 데이터 전체 조회 테스트"() {
        given:
        String recordKey = "test-record-key"

        List<DailyHealthSummaryEntity> entities = [
                createDailyEntity(1L, recordKey, LocalDate.of(2024, 1, 1), 5000, 200.0f, 3.5f),
                createDailyEntity(2L, recordKey, LocalDate.of(2024, 1, 2), 6000, 250.0f, 4.0f)
        ]

        when:
        List<DailySummaryResponse> result = healthQueryService.getDailySummaries(recordKey, null, null)

        then:
        1 * dailySummaryRepository.findByRecordKeyOrderBySummaryDateAsc(recordKey) >> entities

        result.size() == 2
        result[0].recordKey == recordKey
        result[0].steps == 5000
        result[1].steps == 6000
    }

    def "일별 집계 데이터 전체 조회 테스트 - 데이터 없음"() {
        given:
        String recordKey = "empty-record-key"

        when:
        List<DailySummaryResponse> result = healthQueryService.getDailySummaries(recordKey, null, null)

        then:
        1 * dailySummaryRepository.findByRecordKeyOrderBySummaryDateAsc(recordKey) >> []

        result.isEmpty()
    }

    def "일별 집계 데이터 기간 조회 테스트"() {
        given:
        String recordKey = "test-record-key"
        LocalDate startDate = LocalDate.of(2024, 1, 1)
        LocalDate endDate = LocalDate.of(2024, 1, 31)

        List<DailyHealthSummaryEntity> entities = [
                createDailyEntity(1L, recordKey, LocalDate.of(2024, 1, 15), 7000, 300.0f, 5.0f)
        ]

        when:
        List<DailySummaryResponse> result = healthQueryService.getDailySummaries(recordKey, startDate, endDate)

        then:
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDateBetweenOrderBySummaryDateAsc(recordKey, startDate, endDate) >> entities

        result.size() == 1
        result[0].date == LocalDate.of(2024, 1, 15)
    }

    def "특정 일자 집계 데이터 조회 성공 테스트"() {
        given:
        String recordKey = "test-record-key"
        LocalDate date = LocalDate.of(2024, 1, 1)

        DailyHealthSummaryEntity entity = createDailyEntity(1L, recordKey, date, 8000, 350.0f, 6.0f)

        when:
        DailySummaryResponse result = healthQueryService.getDailySummary(recordKey, date)

        then:
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, date) >> Optional.of(entity)

        result != null
        result.recordKey == recordKey
        result.date == date
        result.steps == 8000
        result.calories == 350.0f
        result.distance == 6.0f
    }

    def "특정 일자 집계 데이터 조회 테스트 - 데이터 없음"() {
        given:
        String recordKey = "test-record-key"
        LocalDate date = LocalDate.of(2024, 12, 31)

        when:
        DailySummaryResponse result = healthQueryService.getDailySummary(recordKey, date)

        then:
        1 * dailySummaryRepository.findByRecordKeyAndSummaryDate(recordKey, date) >> Optional.empty()

        result == null
    }

    def "월별 집계 데이터 전체 조회 테스트"() {
        given:
        String recordKey = "test-record-key"

        List<MonthlyHealthSummaryEntity> entities = [
                createMonthlyEntity(1L, recordKey, 2024, 1, 150000, 6000.0f, 100.0f),
                createMonthlyEntity(2L, recordKey, 2024, 2, 140000, 5500.0f, 95.0f)
        ]

        when:
        List<MonthlySummaryResponse> result = healthQueryService.getMonthlySummaries(recordKey, null)

        then:
        1 * monthlySummaryRepository.findByRecordKeyOrderBySummaryYearAscSummaryMonthAsc(recordKey) >> entities

        result.size() == 2
        result[0].year == 2024
        result[0].month == 1
        result[0].steps == 150000
        result[1].month == 2
    }

    def "월별 집계 데이터 전체 조회 테스트 - 데이터 없음"() {
        given:
        String recordKey = "empty-record-key"

        when:
        List<MonthlySummaryResponse> result = healthQueryService.getMonthlySummaries(recordKey, null)

        then:
        1 * monthlySummaryRepository.findByRecordKeyOrderBySummaryYearAscSummaryMonthAsc(recordKey) >> []

        result.isEmpty()
    }

    def "월별 집계 데이터 특정 연도 조회 테스트"() {
        given:
        String recordKey = "test-record-key"
        Integer year = 2024

        List<MonthlyHealthSummaryEntity> entities = [
                createMonthlyEntity(1L, recordKey, 2024, 6, 180000, 7000.0f, 120.0f),
                createMonthlyEntity(2L, recordKey, 2024, 7, 175000, 6800.0f, 115.0f)
        ]

        when:
        List<MonthlySummaryResponse> result = healthQueryService.getMonthlySummaries(recordKey, year)

        then:
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearOrderBySummaryMonthAsc(recordKey, year) >> entities

        result.size() == 2
        result.every { it.year == 2024 }
    }

    def "특정 월 집계 데이터 조회 성공 테스트"() {
        given:
        String recordKey = "test-record-key"
        Integer year = 2024
        Integer month = 3

        MonthlyHealthSummaryEntity entity = createMonthlyEntity(1L, recordKey, year, month, 160000, 6200.0f, 105.0f)

        when:
        MonthlySummaryResponse result = healthQueryService.getMonthlySummary(recordKey, year, month)

        then:
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, year, month) >> Optional.of(entity)

        result != null
        result.recordKey == recordKey
        result.year == 2024
        result.month == 3
        result.steps == 160000
    }

    def "특정 월 집계 데이터 조회 테스트 - 데이터 없음"() {
        given:
        String recordKey = "test-record-key"
        Integer year = 2025
        Integer month = 12

        when:
        MonthlySummaryResponse result = healthQueryService.getMonthlySummary(recordKey, year, month)

        then:
        1 * monthlySummaryRepository.findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, year, month) >> Optional.empty()

        result == null
    }

    // Helper methods
    private DailyHealthSummaryEntity createDailyEntity(Long id, String recordKey, LocalDate date,
                                                        Integer steps, Float calories, Float distance) {
        return DailyHealthSummaryEntity.builder()
                .id(id)
                .recordKey(recordKey)
                .summaryDate(date)
                .totalSteps(steps)
                .totalCalories(calories)
                .totalDistance(distance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
    }

    private MonthlyHealthSummaryEntity createMonthlyEntity(Long id, String recordKey, Integer year, Integer month,
                                                            Integer steps, Float calories, Float distance) {
        return MonthlyHealthSummaryEntity.builder()
                .id(id)
                .recordKey(recordKey)
                .summaryYear(year)
                .summaryMonth(month)
                .totalSteps(steps)
                .totalCalories(calories)
                .totalDistance(distance)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
    }
}
