package com.ocare.domain.health.controller;

import com.ocare.common.response.ApiResponse;
import com.ocare.domain.health.dto.DailySummaryResponse;
import com.ocare.domain.health.dto.HealthDataRequest;
import com.ocare.domain.health.dto.MonthlySummaryResponse;
import com.ocare.domain.health.service.HealthDataService;
import com.ocare.domain.health.service.HealthQueryService;
import com.ocare.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 건강 데이터 API 컨트롤러
 */
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
public class HealthController {

    private final HealthDataService healthDataService;
    private final HealthQueryService healthQueryService;

    /**
     * 건강 데이터 저장
     * POST /api/health/data
     */
    @PostMapping("/data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> saveHealthData(
            @RequestBody HealthDataRequest request) {
        int savedCount = healthDataService.saveHealthData(request);
        return ResponseUtils.created(ApiResponse.success("건강 데이터가 저장되었습니다",
                Map.of(
                        "recordKey", request.getRecordKey(),
                        "savedCount", savedCount
                )));
    }

    /**
     * 일별 집계 데이터 조회
     * GET /api/health/daily?recordKey={recordKey}&startDate={yyyy-MM-dd}&endDate={yyyy-MM-dd}
     */
    @GetMapping("/daily")
    public ResponseEntity<ApiResponse<List<DailySummaryResponse>>> getDailySummaries(
            @RequestParam String recordKey,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<DailySummaryResponse> summaries;
        if (startDate != null && endDate != null) {
            summaries = healthQueryService.getDailySummaries(recordKey, startDate, endDate);
        } else {
            summaries = healthQueryService.getDailySummaries(recordKey);
        }

        return ResponseUtils.ok(ApiResponse.success(summaries));
    }

    /**
     * 특정 일자 집계 데이터 조회
     * GET /api/health/daily/{date}?recordKey={recordKey}
     */
    @GetMapping("/daily/{date}")
    public ResponseEntity<ApiResponse<DailySummaryResponse>> getDailySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String recordKey) {

        DailySummaryResponse summary = healthQueryService.getDailySummary(recordKey, date);
        if (summary == null) {
            return ResponseUtils.ok(ApiResponse.success("해당 날짜의 데이터가 없습니다", null));
        }
        return ResponseUtils.ok(ApiResponse.success(summary));
    }

    /**
     * 월별 집계 데이터 조회
     * GET /api/health/monthly?recordKey={recordKey}&year={yyyy}
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponse<List<MonthlySummaryResponse>>> getMonthlySummaries(
            @RequestParam String recordKey,
            @RequestParam(required = false) Integer year) {

        List<MonthlySummaryResponse> summaries;
        if (year != null) {
            summaries = healthQueryService.getMonthlySummaries(recordKey, year);
        } else {
            summaries = healthQueryService.getMonthlySummaries(recordKey);
        }

        return ResponseUtils.ok(ApiResponse.success(summaries));
    }

    /**
     * 특정 월 집계 데이터 조회
     * GET /api/health/monthly/{year}/{month}?recordKey={recordKey}
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> getMonthlySummary(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @RequestParam String recordKey) {

        MonthlySummaryResponse summary = healthQueryService.getMonthlySummary(recordKey, year, month);
        if (summary == null) {
            return ResponseUtils.ok(ApiResponse.success("해당 월의 데이터가 없습니다", null));
        }
        return ResponseUtils.ok(ApiResponse.success(summary));
    }
}
