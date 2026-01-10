package com.ocare.domain.health.dto;

import com.ocare.domain.health.entity.DailyHealthSummaryEntity;
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

    /**
     * Entity -> DTO 변환
     */
    public static DailySummaryResponse of(DailyHealthSummaryEntity entity) {
        return DailySummaryResponse.builder()
                .recordKey(entity.getRecordKey())
                .date(entity.getSummaryDate())
                .steps(entity.getTotalSteps())
                .calories(entity.getTotalCalories())
                .distance(entity.getTotalDistance())
                .build();
    }
}
