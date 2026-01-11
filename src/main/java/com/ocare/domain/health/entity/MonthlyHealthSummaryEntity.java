package com.ocare.domain.health.entity;

import com.ocare.domain.health.dto.MonthlyAggregation;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "MONTHLY_HEALTH_SUMMARY",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_monthly_summary",
                        columnNames = {"record_key", "summary_year", "summary_month"})
        },
        indexes = {
                @Index(name = "idx_monthly_record_key", columnList = "record_key"),
                @Index(name = "idx_summary_year_month", columnList = "summary_year, summary_month")
        })
public class MonthlyHealthSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    @Column(name = "summary_year", nullable = false)
    private Integer summaryYear;

    @Column(name = "summary_month", nullable = false)
    private Integer summaryMonth;

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

    public static MonthlyHealthSummaryEntity of(String recordKey, MonthlyAggregation agg) {
        LocalDateTime now = LocalDateTime.now();
        return MonthlyHealthSummaryEntity.builder()
                .recordKey(recordKey)
                .summaryYear(agg.getYear())
                .summaryMonth(agg.getMonth())
                .totalSteps(agg.getSteps())
                .totalCalories(agg.getCalories())
                .totalDistance(agg.getDistance())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void updateSummary(MonthlyAggregation agg) {
        this.totalSteps = agg.getSteps();
        this.totalCalories = agg.getCalories();
        this.totalDistance = agg.getDistance();
        this.updatedAt = LocalDateTime.now();
    }
}
