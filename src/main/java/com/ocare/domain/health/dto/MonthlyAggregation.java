package com.ocare.domain.health.dto;

import lombok.Getter;

@Getter
public class MonthlyAggregation {

    private final int year;
    private final int month;
    private int steps = 0;
    private float calories = 0f;
    private float distance = 0f;

    public MonthlyAggregation(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public void add(int steps, float calories, float distance) {
        this.steps += steps;
        this.calories += calories;
        this.distance += distance;
    }
}
