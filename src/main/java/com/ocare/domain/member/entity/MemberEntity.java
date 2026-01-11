package com.ocare.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "MEMBER")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "nickname", nullable = false, unique = true, length = 50)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "record_key", nullable = false, unique = true, length = 36)
    private String recordKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static MemberEntity of(String name, String nickname, String email, String password) {
        LocalDateTime now = LocalDateTime.now();
        return MemberEntity.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .password(password)
                .recordKey(UUID.randomUUID().toString())
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static MemberEntity of(String name, String nickname, String email, String password, String recordKey) {
        LocalDateTime now = LocalDateTime.now();
        return MemberEntity.builder()
                .name(name)
                .nickname(nickname)
                .email(email)
                .password(password)
                .recordKey(recordKey)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
