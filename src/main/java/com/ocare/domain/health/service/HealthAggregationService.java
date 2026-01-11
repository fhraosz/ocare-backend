package com.ocare.domain.health.service;

import com.ocare.domain.health.dto.DailyAggregation;
import com.ocare.domain.health.dto.MonthlyAggregation;
import com.ocare.domain.health.entity.DailyHealthSummaryEntity;
import com.ocare.domain.health.entity.HealthEntryEntity;
import com.ocare.domain.health.entity.MonthlyHealthSummaryEntity;
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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HealthAggregationService {

    private final HealthEntryRepository healthEntryRepository;
    private final DailyHealthSummaryRepository dailySummaryRepository;
    private final MonthlyHealthSummaryRepository monthlySummaryRepository;

    public void updateAggregations(String recordKey) {
        List<HealthEntryEntity> entries = healthEntryRepository.findByRecordKey(recordKey);

        if (entries.isEmpty()) {
            log.info("No entries found for recordKey: {}", recordKey);
            return;
        }

        log.debug("집계 시작: recordKey={}, entryCount={}", recordKey, entries.size());

        Map<LocalDate, DailyAggregation> dailyMap = new HashMap<>();
        Map<String, MonthlyAggregation> monthlyMap = new HashMap<>();

        aggregateEntries(entries, dailyMap, monthlyMap);
        saveDailyAggregations(recordKey, dailyMap);
        saveMonthlyAggregations(recordKey, monthlyMap);

        log.info("집계 완료: recordKey={}, daily={}, monthly={}",
                recordKey, dailyMap.size(), monthlyMap.size());
    }

    private void aggregateEntries(List<HealthEntryEntity> entries,
                                  Map<LocalDate, DailyAggregation> dailyMap,
                                  Map<String, MonthlyAggregation> monthlyMap) {
        for (HealthEntryEntity entry : entries) {
            LocalDate date = entry.getPeriodFrom().toLocalDate();
            int year = date.getYear();
            int month = date.getMonthValue();
            String monthKey = year + "-" + month;

            dailyMap.computeIfAbsent(date, k -> new DailyAggregation())
                    .add(entry.getSteps(), entry.getCalories(), entry.getDistance());

            monthlyMap.computeIfAbsent(monthKey, k -> new MonthlyAggregation(year, month))
                    .add(entry.getSteps(), entry.getCalories(), entry.getDistance());
        }
    }

    private void saveDailyAggregations(String recordKey, Map<LocalDate, DailyAggregation> dailyMap) {
        for (Map.Entry<LocalDate, DailyAggregation> entry : dailyMap.entrySet()) {
            LocalDate date = entry.getKey();
            DailyAggregation agg = entry.getValue();

            Optional<DailyHealthSummaryEntity> existing = dailySummaryRepository
                    .findByRecordKeyAndSummaryDate(recordKey, date);

            if (existing.isPresent()) {
                existing.get().updateSummary(agg);
            } else {
                DailyHealthSummaryEntity summary = DailyHealthSummaryEntity.of(recordKey, date, agg);
                dailySummaryRepository.save(summary);
            }
        }
    }

    private void saveMonthlyAggregations(String recordKey, Map<String, MonthlyAggregation> monthlyMap) {
        for (Map.Entry<String, MonthlyAggregation> entry : monthlyMap.entrySet()) {
            MonthlyAggregation agg = entry.getValue();

            Optional<MonthlyHealthSummaryEntity> existing = monthlySummaryRepository
                    .findByRecordKeyAndSummaryYearAndSummaryMonth(recordKey, agg.getYear(), agg.getMonth());

            if (existing.isPresent()) {
                existing.get().updateSummary(agg);
            } else {
                MonthlyHealthSummaryEntity summary = MonthlyHealthSummaryEntity.of(recordKey, agg);
                monthlySummaryRepository.save(summary);
            }
        }
    }
}
