package com.ocare.domain.health.controller;

import com.ocare.common.util.ResponseUtil;
import com.ocare.domain.health.dto.request.HealthDataRequest;
import com.ocare.domain.health.dto.response.DailySummaryResponse;
import com.ocare.domain.health.dto.response.HealthDataSaveResponse;
import com.ocare.domain.health.dto.response.MonthlySummaryResponse;
import com.ocare.domain.health.service.HealthDataService;
import com.ocare.domain.health.service.HealthQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

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
    public ResponseEntity<HealthDataSaveResponse> saveHealthData(@Valid @RequestBody HealthDataRequest request) {
        return ResponseUtil.created(healthDataService.saveHealthData(request));
    }

    /**
     * 일별 집계 데이터 조회
     * GET /api/health/daily?recordKey={recordKey}&startDate={yyyy-MM-dd}&endDate={yyyy-MM-dd}
     */
    @GetMapping("/daily")
    public ResponseEntity<List<DailySummaryResponse>> getDailySummaries(
            @RequestParam String recordKey,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseUtil.ok(healthQueryService.getDailySummaries(recordKey, startDate, endDate));
    }

    /**
     * 특정 일자 집계 데이터 조회
     * GET /api/health/daily/{date}?recordKey={recordKey}
     */
    @GetMapping("/daily/{date}")
    public ResponseEntity<DailySummaryResponse> getDailySummary(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam String recordKey) {
        return ResponseUtil.okOrNotFound(healthQueryService.getDailySummary(recordKey, date));
    }

    /**
     * 월별 집계 데이터 조회
     * GET /api/health/monthly?recordKey={recordKey}&year={yyyy}
     */
    @GetMapping("/monthly")
    public ResponseEntity<List<MonthlySummaryResponse>> getMonthlySummaries(
            @RequestParam String recordKey,
            @RequestParam(required = false) Integer year) {
        return ResponseUtil.ok(healthQueryService.getMonthlySummaries(recordKey, year));
    }

    /**
     * 특정 월 집계 데이터 조회
     * GET /api/health/monthly/{year}/{month}?recordKey={recordKey}
     */
    @GetMapping("/monthly/{year}/{month}")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @PathVariable Integer year,
            @PathVariable Integer month,
            @RequestParam String recordKey) {
        return ResponseUtil.okOrNotFound(healthQueryService.getMonthlySummary(recordKey, year, month));
    }
}
