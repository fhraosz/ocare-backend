package com.ocare.domain.health.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntryDto {
    private PeriodDto period;
    private ValueDto distance;
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
