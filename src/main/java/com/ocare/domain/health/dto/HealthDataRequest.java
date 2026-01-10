package com.ocare.domain.health.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 건강 데이터 JSON 입력 DTO
 * INPUT_DATA1~4.json 파일 구조에 맞춤
 */
@Getter
@Setter
@NoArgsConstructor
public class HealthDataRequest {

    @JsonProperty("recordkey")
    private String recordKey;

    private DataWrapper data;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DataWrapper {
        private String memo;
        private List<EntryDto> entries;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class EntryDto {
        private PeriodDto period;
        private ValueDto distance;
        private ValueDto calories;
        // steps가 int 또는 String(소수점)으로 올 수 있음
        private Object steps;

        /**
         * steps 값을 Integer로 변환
         * - int인 경우: 그대로 반환
         * - String(소수점)인 경우: Double 파싱 후 반올림
         */
        public Integer getStepsAsInteger() {
            if (steps == null) {
                return 0;
            }
            if (steps instanceof Number) {
                return ((Number) steps).intValue();
            }
            if (steps instanceof String) {
                try {
                    double value = Double.parseDouble((String) steps);
                    return (int) Math.round(value);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
            return 0;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class PeriodDto {
        private String from;
        private String to;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class ValueDto {
        private String unit;
        private Object value;

        /**
         * value를 Float로 변환
         */
        public Float getValueAsFloat() {
            if (value == null) {
                return 0f;
            }
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            if (value instanceof String) {
                try {
                    return Float.parseFloat((String) value);
                } catch (NumberFormatException e) {
                    return 0f;
                }
            }
            return 0f;
        }
    }
}
