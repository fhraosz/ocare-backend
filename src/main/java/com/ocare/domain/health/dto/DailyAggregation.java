package com.ocare.domain.health.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DailyAggregation {

    private int steps = 0;
    private float calories = 0f;
    private float distance = 0f;

    public void add(int steps, float calories, float distance) {
        this.steps += steps;
        this.calories += calories;
        this.distance += distance;
    }
}
