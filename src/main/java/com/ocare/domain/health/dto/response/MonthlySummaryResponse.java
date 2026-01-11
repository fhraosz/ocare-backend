package com.ocare.domain.health.dto.response;

import com.ocare.domain.health.entity.MonthlyHealthSummaryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummaryResponse {

    private String recordKey;
    private Integer year;
    private Integer month;
    private String yearMonth;
    private Integer steps;
    private Float calories;
    private Float distance;

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
