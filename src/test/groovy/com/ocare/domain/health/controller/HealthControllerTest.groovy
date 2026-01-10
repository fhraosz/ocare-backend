package com.ocare.domain.health.controller

import com.ocare.common.response.ApiResponse
import com.ocare.domain.health.dto.DailySummaryResponse
import com.ocare.domain.health.dto.HealthDataRequest
import com.ocare.domain.health.dto.MonthlySummaryResponse
import com.ocare.domain.health.service.HealthDataService
import com.ocare.domain.health.service.HealthQueryService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate

class HealthControllerTest extends Specification {

    HealthDataService healthDataService = Mock()
    HealthQueryService healthQueryService = Mock()

    @Subject
    HealthController healthController = new HealthController(healthDataService, healthQueryService)

    def "건강 데이터 저장 API 성공 테스트"() {
        given:
        HealthDataRequest request = new HealthDataRequest()
        request.recordKey = "test-record-key"
        request.data = new HealthDataRequest.DataWrapper()
        request.data.entries = []

        when:
        ResponseEntity<ApiResponse<Map<String, Object>>> result = healthController.saveHealthData(request)

        then:
        1 * healthDataService.saveHealthData(request) >> 5

        result.statusCode == HttpStatus.CREATED
        result.body.success == true
        result.body.message == "건강 데이터가 저장되었습니다"
        result.body.data.recordKey == "test-record-key"
        result.body.data.savedCount == 5
    }

    def "일별 집계 데이터 조회 API 테스트 - 전체 조회"() {
        given:
        String recordKey = "test-record-key"

        List<DailySummaryResponse> summaries = [
                DailySummaryResponse.builder()
                        .recordKey(recordKey)
                        .date(LocalDate.of(2024, 1, 1))
                        .steps(5000)
                        .calories(200.0f)
                        .distance(3.5f)
                        .build(),
                DailySummaryResponse.builder()
                        .recordKey(recordKey)
                        .date(LocalDate.of(2024, 1, 2))
                        .steps(6000)
                        .calories(250.0f)
                        .distance(4.0f)
                        .build()
        ]

        when:
        ResponseEntity<ApiResponse<List<DailySummaryResponse>>> result =
                healthController.getDailySummaries(recordKey, null, null)

        then:
        1 * healthQueryService.getDailySummaries(recordKey) >> summaries

        result.statusCode == HttpStatus.OK
        result.body.success == true
        result.body.data.size() == 2
    }

    def "일별 집계 데이터 조회 API 테스트 - 기간 조회"() {
        given:
        String recordKey = "test-record-key"
        LocalDate startDate = LocalDate.of(2024, 1, 1)
        LocalDate endDate = LocalDate.of(2024, 1, 31)

        List<DailySummaryResponse> summaries = [
                DailySummaryResponse.builder()
                        .recordKey(recordKey)
                        .date(LocalDate.of(2024, 1, 15))
                        .steps(7000)
                        .calories(300.0f)
                        .distance(5.0f)
                        .build()
        ]

        when:
        ResponseEntity<ApiResponse<List<DailySummaryResponse>>> result =
                healthController.getDailySummaries(recordKey, startDate, endDate)

        then:
        1 * healthQueryService.getDailySummaries(recordKey, startDate, endDate) >> summaries

        result.statusCode == HttpStatus.OK
        result.body.data.size() == 1
    }

    def "특정 일자 집계 데이터 조회 API 성공 테스트"() {
        given:
        String recordKey = "test-record-key"
        LocalDate date = LocalDate.of(2024, 1, 1)

        DailySummaryResponse summary = DailySummaryResponse.builder()
                .recordKey(recordKey)
                .date(date)
                .steps(8000)
                .calories(350.0f)
                .distance(6.0f)
                .build()

        when:
        ResponseEntity<ApiResponse<DailySummaryResponse>> result =
                healthController.getDailySummary(date, recordKey)

        then:
        1 * healthQueryService.getDailySummary(recordKey, date) >> summary

        result.statusCode == HttpStatus.OK
        result.body.success == true
        result.body.data.steps == 8000
    }

    def "특정 일자 집계 데이터 조회 API 테스트 - 데이터 없음"() {
        given:
        String recordKey = "test-record-key"
        LocalDate date = LocalDate.of(2024, 12, 31)

        when:
        ResponseEntity<ApiResponse<DailySummaryResponse>> result =
                healthController.getDailySummary(date, recordKey)

        then:
        1 * healthQueryService.getDailySummary(recordKey, date) >> null

        result.statusCode == HttpStatus.OK
        result.body.success == true
        result.body.message == "해당 날짜의 데이터가 없습니다"
        result.body.data == null
    }

    def "월별 집계 데이터 조회 API 테스트 - 전체 조회"() {
        given:
        String recordKey = "test-record-key"

        List<MonthlySummaryResponse> summaries = [
                MonthlySummaryResponse.builder()
                        .recordKey(recordKey)
                        .year(2024)
                        .month(1)
                        .steps(150000)
                        .calories(6000.0f)
                        .distance(100.0f)
                        .build()
        ]

        when:
        ResponseEntity<ApiResponse<List<MonthlySummaryResponse>>> result =
                healthController.getMonthlySummaries(recordKey, null)

        then:
        1 * healthQueryService.getMonthlySummaries(recordKey) >> summaries

        result.statusCode == HttpStatus.OK
        result.body.data.size() == 1
    }

    def "월별 집계 데이터 조회 API 테스트 - 특정 연도"() {
        given:
        String recordKey = "test-record-key"
        Integer year = 2024

        List<MonthlySummaryResponse> summaries = [
                MonthlySummaryResponse.builder()
                        .recordKey(recordKey)
                        .year(2024)
                        .month(6)
                        .steps(180000)
                        .calories(7000.0f)
                        .distance(120.0f)
                        .build()
        ]

        when:
        ResponseEntity<ApiResponse<List<MonthlySummaryResponse>>> result =
                healthController.getMonthlySummaries(recordKey, year)

        then:
        1 * healthQueryService.getMonthlySummaries(recordKey, year) >> summaries

        result.statusCode == HttpStatus.OK
        result.body.data[0].year == 2024
    }

    def "특정 월 집계 데이터 조회 API 성공 테스트"() {
        given:
        String recordKey = "test-record-key"
        Integer year = 2024
        Integer month = 3

        MonthlySummaryResponse summary = MonthlySummaryResponse.builder()
                .recordKey(recordKey)
                .year(year)
                .month(month)
                .steps(160000)
                .calories(6200.0f)
                .distance(105.0f)
                .build()

        when:
        ResponseEntity<ApiResponse<MonthlySummaryResponse>> result =
                healthController.getMonthlySummary(year, month, recordKey)

        then:
        1 * healthQueryService.getMonthlySummary(recordKey, year, month) >> summary

        result.statusCode == HttpStatus.OK
        result.body.data.year == 2024
        result.body.data.month == 3
        result.body.data.steps == 160000
    }

    def "특정 월 집계 데이터 조회 API 테스트 - 데이터 없음"() {
        given:
        String recordKey = "test-record-key"
        Integer year = 2025
        Integer month = 12

        when:
        ResponseEntity<ApiResponse<MonthlySummaryResponse>> result =
                healthController.getMonthlySummary(year, month, recordKey)

        then:
        1 * healthQueryService.getMonthlySummary(recordKey, year, month) >> null

        result.statusCode == HttpStatus.OK
        result.body.message == "해당 월의 데이터가 없습니다"
        result.body.data == null
    }
}
