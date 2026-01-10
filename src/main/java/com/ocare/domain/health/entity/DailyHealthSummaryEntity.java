package com.ocare.domain.health.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 건강 데이터 집계 엔티티
 * 과제 요구사항의 Daily 집계 테이블
 */
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

    /**
     * 정적 팩토리 메서드
     */
    public static DailyHealthSummaryEntity of(String recordKey, LocalDate summaryDate,
                                               Integer totalSteps, Float totalCalories, Float totalDistance) {
        LocalDateTime now = LocalDateTime.now();
        return DailyHealthSummaryEntity.builder()
                .recordKey(recordKey)
                .summaryDate(summaryDate)
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
