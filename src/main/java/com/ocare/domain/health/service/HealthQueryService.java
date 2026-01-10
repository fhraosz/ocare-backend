package com.ocare.domain.health.service;

import com.ocare.domain.health.dto.DailySummaryResponse;
import com.ocare.domain.health.dto.MonthlySummaryResponse;
import com.ocare.domain.health.entity.DailyHealthSummary;
import com.ocare.domain.health.entity.MonthlyHealthSummary;
import com.ocare.domain.health.repository.DailyHealthSummaryRepository;
import com.ocare.domain.health.repository.MonthlyHealthSummaryRepository;
import lombok.RequiredArgsConstructor;
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
public class HealthQueryService {

    private final DailyHealthSummaryRepository dailySummaryRepository;
    private final MonthlyHealthSummaryRepository monthlySummaryRepository;

    /**
     * 일별 집계 데이터 조회 (전체)
     */
    public List<DailySummaryResponse> getDailySummaries(String recordKey) {
        return dailySummaryRepository.findByRecordKeyOrderBySummaryDateAsc(recordKey)
                .stream()
                .map(DailySummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 일별 집계 데이터 조회 (기간)
     */
    public List<DailySummaryResponse> getDailySummaries(String recordKey,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
        return dailySummaryRepository
                .findByRecordKeyAndSummaryDateBetweenOrderBySummaryDateAsc(recordKey, startDate, endDate)
                .stream()
                .map(DailySummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 월별 집계 데이터 조회 (전체)
     */
    public List<MonthlySummaryResponse> getMonthlySummaries(String recordKey) {
        return monthlySummaryRepository.findByRecordKeyOrderBySummaryYearAscSummaryMonthAsc(recordKey)
                .stream()
                .map(MonthlySummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 월별 집계 데이터 조회 (특정 연도)
     */
    public List<MonthlySummaryResponse> getMonthlySummaries(String recordKey, Integer year) {
        return monthlySummaryRepository.findByRecordKeyAndSummaryYearOrderBySummaryMonthAsc(recordKey, year)
                .stream()
                .map(MonthlySummaryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 특정 월 집계 데이터 조회
     */
    public MonthlySummaryResponse getMonthlySummary(String recordKey, Integer year, Integer month) {
        return monthlySummaryRepository
                .findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, year, month)
                .map(MonthlySummaryResponse::from)
                .orElse(null);
    }

    /**
     * 특정 일 집계 데이터 조회
     */
    public DailySummaryResponse getDailySummary(String recordKey, LocalDate date) {
        return dailySummaryRepository
                .findByRecordKeyAndSummaryDate(recordKey, date)
                .map(DailySummaryResponse::from)
                .orElse(null);
    }
}
