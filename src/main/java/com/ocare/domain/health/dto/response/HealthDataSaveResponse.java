package com.ocare.domain.health.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class HealthDataSaveResponse {

    private String recordKey;
    private int savedCount;

    public static HealthDataSaveResponse of(String recordKey, int savedCount) {
        return new HealthDataSaveResponse(recordKey, savedCount);
    }
}
