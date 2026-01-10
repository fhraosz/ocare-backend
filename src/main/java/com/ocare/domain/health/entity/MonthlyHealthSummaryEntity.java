package com.ocare.domain.health.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 월별 건강 데이터 집계 엔티티
 * 과제 요구사항의 Monthly 집계 테이블
 */
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

    /**
     * 정적 팩토리 메서드
     */
    public static MonthlyHealthSummaryEntity of(String recordKey, Integer summaryYear, Integer summaryMonth,
                                                 Integer totalSteps, Float totalCalories, Float totalDistance) {
        LocalDateTime now = LocalDateTime.now();
        return MonthlyHealthSummaryEntity.builder()
                .recordKey(recordKey)
                .summaryYear(summaryYear)
                .summaryMonth(summaryMonth)
                .totalSteps(totalSteps)
                .totalCalories(totalCalories)
                .totalDistance(totalDistance)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    /**
     * 집계 데이터 갱신
     */
    public void updateSummary(Integer totalSteps, Float totalCalories, Float totalDistance) {
        this.totalSteps = totalSteps;
        this.totalCalories = totalCalories;
        this.totalDistance = totalDistance;
        this.updatedAt = LocalDateTime.now();
    }
}
