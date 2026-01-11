package com.ocare.domain.health.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "HEALTH_ENTRY",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_health_entry",
                        columnNames = {"record_key", "period_from", "period_to"})
        },
        indexes = {
                @Index(name = "idx_entry_record_key", columnList = "record_key"),
                @Index(name = "idx_entry_period_from", columnList = "period_from")
        })
public class HealthEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_key", nullable = false, length = 36)
    private String recordKey;

    @Column(name = "period_from", nullable = false)
    private LocalDateTime periodFrom;

    @Column(name = "period_to", nullable = false)
    private LocalDateTime periodTo;

    @Column(name = "steps", nullable = false)
    private Integer steps;

    @Column(name = "calories", nullable = false)
    private Float calories;

    @Column(name = "distance", nullable = false)
    private Float distance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static HealthEntryEntity of(String recordKey, LocalDateTime periodFrom, LocalDateTime periodTo,
                                       Integer steps, Float calories, Float distance) {
        return HealthEntryEntity.builder()
                .recordKey(recordKey)
                .periodFrom(periodFrom)
                .periodTo(periodTo)
                .steps(steps)
                .calories(calories)
                .distance(distance)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void update(Integer steps, Float calories, Float distance) {
        this.steps = steps;
        this.calories = calories;
        this.distance = distance;
    }
}
