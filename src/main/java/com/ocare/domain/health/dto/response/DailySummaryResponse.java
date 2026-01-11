package com.ocare.domain.health.dto.response;

import com.ocare.domain.health.entity.DailyHealthSummaryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailySummaryResponse {

    private String recordKey;
    private LocalDate date;
    private Integer steps;
    private Float calories;
    private Float distance;

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
