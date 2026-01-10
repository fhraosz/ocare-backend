package com.ocare.domain.health.dto;

import com.ocare.domain.health.entity.MonthlyHealthSummaryEntity;
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
    private String yearMonth;
    private Integer steps;
    private Float calories;
    private Float distance;

    /**
     * Entity -> DTO 변환
     */
    public static MonthlySummaryResponse of(MonthlyHealthSummaryEntity entity) {
        return MonthlySummaryResponse.builder()
                .recordKey(entity.getRecordKey())
                .year(entity.getSummaryYear())
                .month(entity.getSummaryMonth())
                .yearMonth(String.format("%d-%02d", entity.getSummaryYear(), entity.getSummaryMonth()))
                .steps(entity.getTotalSteps())
                .calories(entity.getTotalCalories())
                .distance(entity.getTotalDistance())
                .build();
    }
}
