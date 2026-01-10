package com.ocare.domain.health.service;

import com.ocare.domain.health.dto.DailySummaryResponse;
import com.ocare.domain.health.dto.MonthlySummaryResponse;
import com.ocare.domain.health.repository.DailyHealthSummaryRepository;
import com.ocare.domain.health.repository.MonthlyHealthSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 건강 데이터 조회 서비스
 * 일별/월별 집계 데이터 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HealthQueryService {

    private final DailyHealthSummaryRepository dailySummaryRepository;
    private final MonthlyHealthSummaryRepository monthlySummaryRepository;

    /**
     * 일별 집계 데이터 조회 (전체)
     */
    public List<DailySummaryResponse> getDailySummaries(String recordKey) {
        log.debug("일별 집계 조회: recordKey={}", recordKey);
        return dailySummaryRepository.findByRecordKeyOrderBySummaryDateAsc(recordKey)
                .stream()
                .map(DailySummaryResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 일별 집계 데이터 조회 (기간)
     */
    public List<DailySummaryResponse> getDailySummaries(String recordKey,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
        log.debug("일별 집계 조회: recordKey={}, startDate={}, endDate={}", recordKey, startDate, endDate);
        return dailySummaryRepository
                .findByRecordKeyAndSummaryDateBetweenOrderBySummaryDateAsc(recordKey, startDate, endDate)
                .stream()
                .map(DailySummaryResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 월별 집계 데이터 조회 (전체)
     */
    public List<MonthlySummaryResponse> getMonthlySummaries(String recordKey) {
        log.debug("월별 집계 조회: recordKey={}", recordKey);
        return monthlySummaryRepository.findByRecordKeyOrderBySummaryYearAscSummaryMonthAsc(recordKey)
                .stream()
                .map(MonthlySummaryResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 월별 집계 데이터 조회 (특정 연도)
     */
    public List<MonthlySummaryResponse> getMonthlySummaries(String recordKey, Integer year) {
        log.debug("월별 집계 조회: recordKey={}, year={}", recordKey, year);
        return monthlySummaryRepository.findByRecordKeyAndSummaryYearOrderBySummaryMonthAsc(recordKey, year)
                .stream()
                .map(MonthlySummaryResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 특정 월 집계 데이터 조회
     */
    public MonthlySummaryResponse getMonthlySummary(String recordKey, Integer year, Integer month) {
        log.debug("특정 월 집계 조회: recordKey={}, year={}, month={}", recordKey, year, month);
        return monthlySummaryRepository
                .findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, year, month)
                .map(MonthlySummaryResponse::of)
                .orElse(null);
    }

    /**
     * 특정 일 집계 데이터 조회
     */
    public DailySummaryResponse getDailySummary(String recordKey, LocalDate date) {
        log.debug("특정 일 집계 조회: recordKey={}, date={}", recordKey, date);
        return dailySummaryRepository
                .findByRecordKeyAndSummaryDate(recordKey, date)
                .map(DailySummaryResponse::of)
                .orElse(null);
    }
}
