package com.ocare.domain.health.dto;

import com.ocare.domain.health.entity.DailyHealthSummary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * 일별 집계 응답 DTO
 */
@Getter
@Builder
public class DailySummaryResponse {

    private String recordKey;
    private LocalDate date;
    private Integer steps;
    private Float calories;
    private Float distance;

    public static DailySummaryResponse from(DailyHealthSummary summary) {
        return DailySummaryResponse.builder()
                .recordKey(summary.getRecordKey())
                .date(summary.getSummaryDate())
                .steps(summary.getTotalSteps())
                .calories(summary.getTotalCalories())
                .distance(summary.getTotalDistance())
                .build();
    }
}
