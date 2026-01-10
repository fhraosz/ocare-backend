package com.ocare.domain.health.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 건강 데이터 원본 엔티티 (10분 단위)
 * 삼성헬스/애플건강에서 수집된 원본 데이터 저장
 */
@Entity
@Table(name = "health_entries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_health_entry",
                        columnNames = {"record_key", "period_from", "period_to"})
        },
        indexes = {
                @Index(name = "idx_record_key", columnList = "record_key"),
                @Index(name = "idx_period_from", columnList = "period_from")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class HealthEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 사용자 구분 키 (UUID)
    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    // 측정 시작 시간
    @Column(name = "period_from", nullable = false)
    private LocalDateTime periodFrom;

    // 측정 종료 시간
    @Column(name = "period_to", nullable = false)
    private LocalDateTime periodTo;

    // 걸음수
    @Column(nullable = false)
    private Integer steps;

    // 소모 칼로리 (kcal)
    @Column(nullable = false)
    private Float calories;

    // 이동 거리 (km)
    @Column(nullable = false)
    private Float distance;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public HealthEntry(String recordKey, LocalDateTime periodFrom, LocalDateTime periodTo,
                       Integer steps, Float calories, Float distance) {
        this.recordKey = recordKey;
        this.periodFrom = periodFrom;
        this.periodTo = periodTo;
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
    }

    /**
     * 데이터 업데이트 (중복 시 갱신용)
     */
    public void update(Integer steps, Float calories, Float distance) {
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
    }
}
