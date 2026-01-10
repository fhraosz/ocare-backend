package com.ocare.domain.member.dto;

import com.ocare.domain.member.entity.MemberEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 회원 정보 응답 DTO
 */
@Getter
@Builder
public class MemberResponse {

    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String recordKey;
    private LocalDateTime createdAt;

    /**
     * Entity -> DTO 변환
     */
    public static MemberResponse of(MemberEntity entity) {
        return MemberResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .email(entity.getEmail())
                .recordKey(entity.getRecordKey())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
