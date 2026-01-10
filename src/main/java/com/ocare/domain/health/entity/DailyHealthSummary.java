package com.ocare.domain.health.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일별 건강 데이터 집계 엔티티
 * 과제 요구사항의 Daily 집계 테이블
 */
@Entity
@Table(name = "daily_health_summary",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_summary",
                        columnNames = {"record_key", "summary_date"})
        },
        indexes = {
                @Index(name = "idx_daily_record_key", columnList = "record_key"),
                @Index(name = "idx_summary_date", columnList = "summary_date")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class DailyHealthSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 구분 키 (UUID)
    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    // 집계 날짜
    @Column(name = "summary_date", nullable = false)
    private LocalDate summaryDate;

    // 일일 총 걸음수
    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps;

    // 일일 총 소모 칼로리 (kcal)
    @Column(name = "total_calories", nullable = false)
    private Float totalCalories;

    // 일일 총 이동 거리 (km)
    @Column(name = "total_distance", nullable = false)
    private Float totalDistance;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public DailyHealthSummary(String recordKey, LocalDate summaryDate,
                              Integer totalSteps, Float totalCalories, Float totalDistance) {
        this.recordKey = recordKey;
        this.summaryDate = summaryDate;
        this.totalSteps = totalSteps;
        this.totalCalories = totalCalories;
        this.totalDistance = totalDistance;
    }

    /**
     * 집계 데이터 갱신
     */
    public void updateSummary(Integer totalSteps, Float totalCalories, Float totalDistance) {
        this.totalSteps = totalSteps;
        this.totalCalories = totalCalories;
        this.totalDistance = totalDistance;
    }
}
