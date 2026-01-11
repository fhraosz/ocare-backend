package com.ocare.domain.health.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 건강 데이터 저장 응답 DTO
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthDataSaveResponse {

    private String recordKey;
    private int savedCount;

    public static HealthDataSaveResponse of(String recordKey, int savedCount) {
        return new HealthDataSaveResponse(recordKey, savedCount);
    }
}
