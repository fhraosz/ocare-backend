package com.ocare.domain.health.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 월별 건강 데이터 집계 엔티티
 * 과제 요구사항의 Monthly 집계 테이블
 */
@Entity
@Table(name = "monthly_health_summary",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_monthly_summary",
                        columnNames = {"record_key", "summary_year", "summary_month"})
        },
        indexes = {
                @Index(name = "idx_monthly_record_key", columnList = "record_key"),
                @Index(name = "idx_summary_year_month", columnList = "summary_year, summary_month")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MonthlyHealthSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 구분 키 (UUID)
    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    // 집계 연도
    @Column(name = "summary_year", nullable = false)
    private Integer summaryYear;

    // 집계 월
    @Column(name = "summary_month", nullable = false)
    private Integer summaryMonth;

    // 월간 총 걸음수
    @Column(name = "total_steps", nullable = false)
    private Integer totalSteps;

    // 월간 총 소모 칼로리 (kcal)
    @Column(name = "total_calories", nullable = false)
    private Float totalCalories;

    // 월간 총 이동 거리 (km)
    @Column(name = "total_distance", nullable = false)
    private Float totalDistance;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public MonthlyHealthSummary(String recordKey, Integer summaryYear, Integer summaryMonth,
                                Integer totalSteps, Float totalCalories, Float totalDistance) {
        this.recordKey = recordKey;
        this.summaryYear = summaryYear;
        this.summaryMonth = summaryMonth;
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
