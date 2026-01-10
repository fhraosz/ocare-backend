package com.ocare.domain.health.dto;

import com.ocare.domain.health.entity.MonthlyHealthSummary;
import lombok.Builder;
import lombok.Getter;

/**
 * 월별 집계 응답 DTO
 */
@Getter
@Builder
public class MonthlySummaryResponse {

    private String recordKey;
    private Integer year;
    private Integer month;
    private String yearMonth;  // "2024-11" 형식
    private Integer steps;
    private Float calories;
    private Float distance;

    public static MonthlySummaryResponse from(MonthlyHealthSummary summary) {
        return MonthlySummaryResponse.builder()
                .recordKey(summary.getRecordKey())
                .year(summary.getSummaryYear())
                .month(summary.getSummaryMonth())
                .yearMonth(String.format("%d-%02d", summary.getSummaryYear(), summary.getSummaryMonth()))
                .steps(summary.getTotalSteps())
                .calories(summary.getTotalCalories())
                .distance(summary.getTotalDistance())
                .build();
    }
}
