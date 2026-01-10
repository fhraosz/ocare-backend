package com.ocare.domain.member.dto;

import com.ocare.domain.member.entity.Member;
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

    public static MemberResponse from(Member member) {
        return MemberResponse.builder()
                .id(member.getId())
                .name(member.getName())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .recordKey(member.getRecordKey())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
