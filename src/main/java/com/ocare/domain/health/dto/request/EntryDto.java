package com.ocare.domain.health.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {

    @NotNull(message = "period는 필수입니다")
    @Valid
    private PeriodDto period;

    @Valid
    private ValueDto distance;

    @Valid
    private ValueDto calories;

    private Object steps;

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
