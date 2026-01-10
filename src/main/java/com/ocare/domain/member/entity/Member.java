package com.ocare.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원 엔티티
 * 사용자 정보 및 건강 데이터 연동 키 관리
 */
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    // 건강 데이터 연동 키 (UUID) - JSON 데이터의 recordkey와 매핑
    @Column(name = "record_key", nullable = false, unique = true, length = 36)
    private String recordKey;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Member(String name, String nickname, String email, String password) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.recordKey = UUID.randomUUID().toString();
    }

    // recordKey를 직접 지정하는 생성자 (기존 데이터 연동용)
    @Builder(builderMethodName = "withRecordKey", buildMethodName = "buildWithRecordKey")
    public Member(String name, String nickname, String email, String password, String recordKey) {
        this.name = name;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.recordKey = recordKey;
    }
}
