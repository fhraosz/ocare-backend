package com.ocare.domain.health.service;

import com.ocare.domain.health.entity.DailyHealthSummary;
import com.ocare.domain.health.entity.HealthEntry;
import com.ocare.domain.health.entity.MonthlyHealthSummary;
import com.ocare.domain.health.repository.DailyHealthSummaryRepository;
import com.ocare.domain.health.repository.HealthEntryRepository;
import com.ocare.domain.health.repository.MonthlyHealthSummaryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 건강 데이터 집계 서비스
 * 일별/월별 집계 로직
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HealthAggregationService {

    private final HealthEntryRepository healthEntryRepository;
    private final DailyHealthSummaryRepository dailySummaryRepository;
    private final MonthlyHealthSummaryRepository monthlySummaryRepository;

    /**
     * 특정 recordKey의 모든 집계 데이터 갱신
     */
    public void updateAggregations(String recordKey) {
        List<HealthEntry> entries = healthEntryRepository.findByRecordKey(recordKey);

        if (entries.isEmpty()) {
            log.info("No entries found for recordKey: {}", recordKey);
            return;
        }

        // 일별 집계 계산
        Map<LocalDate, DailyAggregation> dailyMap = new HashMap<>();
        // 월별 집계 계산
        Map<String, MonthlyAggregation> monthlyMap = new HashMap<>();

        for (HealthEntry entry : entries) {
            LocalDate date = entry.getPeriodFrom().toLocalDate();
            int year = date.getYear();
            int month = date.getMonthValue();
            String monthKey = year + "-" + month;

            // 일별 집계
            dailyMap.computeIfAbsent(date, k -> new DailyAggregation())
                    .add(entry.getSteps(), entry.getCalories(), entry.getDistance());

            // 월별 집계
            monthlyMap.computeIfAbsent(monthKey, k -> new MonthlyAggregation(year, month))
                    .add(entry.getSteps(), entry.getCalories(), entry.getDistance());
        }

        // 일별 집계 저장
        for (Map.Entry<LocalDate, DailyAggregation> entry : dailyMap.entrySet()) {
            LocalDate date = entry.getKey();
            DailyAggregation agg = entry.getValue();

            Optional<DailyHealthSummary> existing = dailySummaryRepository
                    .findByRecordKeyAndSummaryDate(recordKey, date);

            if (existing.isPresent()) {
                existing.get().updateSummary(agg.steps, agg.calories, agg.distance);
            } else {
                DailyHealthSummary summary = DailyHealthSummary.builder()
                        .recordKey(recordKey)
                        .summaryDate(date)
                        .totalSteps(agg.steps)
                        .totalCalories(agg.calories)
                        .totalDistance(agg.distance)
                        .build();
                dailySummaryRepository.save(summary);
            }
        }

        // 월별 집계 저장
        for (Map.Entry<String, MonthlyAggregation> entry : monthlyMap.entrySet()) {
            MonthlyAggregation agg = entry.getValue();

            Optional<MonthlyHealthSummary> existing = monthlySummaryRepository
                    .findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, agg.year, agg.month);

            if (existing.isPresent()) {
                existing.get().updateSummary(agg.steps, agg.calories, agg.distance);
            } else {
                MonthlyHealthSummary summary = MonthlyHealthSummary.builder()
                        .recordKey(recordKey)
                        .summaryYear(agg.year)
                        .summaryMonth(agg.month)
                        .totalSteps(agg.steps)
                        .totalCalories(agg.calories)
                        .totalDistance(agg.distance)
                        .build();
                monthlySummaryRepository.save(summary);
            }
        }

        log.info("Updated aggregations for recordKey: {} - {} daily, {} monthly",
                recordKey, dailyMap.size(), monthlyMap.size());
    }

    // 내부 집계용 클래스
    private static class DailyAggregation {
        int steps = 0;
        float calories = 0f;
        float distance = 0f;

        void add(int s, float c, float d) {
            this.steps += s;
            this.calories += c;
            this.distance += d;
        }
    }

    private static class MonthlyAggregation {
        int year;
        int month;
        int steps = 0;
        float calories = 0f;
        float distance = 0f;

        MonthlyAggregation(int year, int month) {
            this.year = year;
            this.month = month;
        }

        void add(int s, float c, float d) {
            this.steps += s;
            this.calories += c;
            this.distance += d;
        }
    }
}
