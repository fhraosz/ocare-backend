package com.ocare.domain.health.entity;

import com.ocare.domain.health.dto.DailyAggregation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "DAILY_HEALTH_SUMMARY",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_summary",
                        columnNames = {"record_key", "summary_date"})
        },
        indexes = {
                @Index(name = "idx_daily_record_key", columnList = "record_key"),
                @Index(name = "idx_summary_date", columnList = "summary_date")
        })
public class DailyHealthSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps;

    @Column(name = "total_calories", nullable = false)
    private Float totalCalories;

    @Column(name = "total_distance", nullable = false)
    private Float totalDistance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static DailyHealthSummaryEntity of(String recordKey, LocalDate summaryDate, DailyAggregation agg) {
        LocalDateTime now = LocalDateTime.now();
        return DailyHealthSummaryEntity.builder()
                .recordKey(recordKey)
                .summaryDate(summaryDate)
                .totalSteps(agg.getSteps())
                .totalCalories(agg.getCalories())
                .totalDistance(agg.getDistance())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateSummary(DailyAggregation agg) {
        this.totalSteps = agg.getSteps();
        this.totalCalories = agg.getCalories();
        this.totalDistance = agg.getDistance();
        this.updatedAt = LocalDateTime.now();
    }
}
